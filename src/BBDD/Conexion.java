/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BBDD;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author destro
 */
public class Conexion {
    static Connection conn;


    public static Connection conectar() {

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://145.14.151.1:3306/u812167471_Libreria_3",
                    "u812167471_Libreria_3", "Libreria_3");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public static void cerrarConexion() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean acceder(String user, String pass) {
        try {
            // Modifico la consulta para usar "contraseña" en lugar de "pass"
            String consulta = "SELECT usuario, contraseña FROM vendedores WHERE usuario=? AND contraseña=?";

            PreparedStatement pst;
            ResultSet rs;

            pst = conn.prepareStatement(consulta);

            pst.setString(1, user);
            pst.setString(2, pass);

            rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // En la clase BBDD.Conexion
    public static void consultarResumenVentas(javax.swing.JLabel jLabel4, javax.swing.JLabel jLabel5, javax.swing.JLabel jLabel6) {
        try {
            // Usando la consulta proporcionada
            String consulta = "SELECT COUNT(l.idLibro) AS LIBROS, " +
                    "SUM(l.stock) AS VOLUMEN, " +
                    "(SELECT COUNT(*) FROM ventas_online) + (SELECT COUNT(*) FROM ventas_tienda) AS TOTAL_VENTAS " +
                    "FROM libros l";

            PreparedStatement pst = conn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Actualizamos directamente los labels con los valores
                jLabel4.setText(String.valueOf(rs.getInt("LIBROS")));
                jLabel5.setText(String.valueOf(rs.getInt("VOLUMEN")));
                jLabel6.setText(String.valueOf(rs.getInt("TOTAL_VENTAS")));
            } else {
                // No hay datos, establecer valores a cero
                jLabel4.setText("0");
                jLabel5.setText("0");
                jLabel6.setText("0");
            }

            rs.close();
            pst.close();

        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            // En caso de error, ponemos valores por defecto
            jLabel4.setText("0");
            jLabel5.setText("0");
            jLabel6.setText("0");
        }
    }

    public static void TablaTienda(DefaultTableModel modelo) {
        Object[] datos = new Object[3];

        String consulta = "SELECT \n" +
                "    a.nombre AS AUTOR, \n" +
                "    l.titulo AS TITULO, \n" +
                "    COUNT(vt.idVenta) AS VENTAS\n" +
                "FROM autores a\n" +
                "JOIN libros l ON a.idAutor = l.idAutor\n" +
                "JOIN ventas_tienda vt ON l.idLibro = vt.idLibro\n" +
                "GROUP BY a.nombre, l.titulo\n" +
                "ORDER BY VENTAS DESC\n" +
                "LIMIT 3;\n";
        try {
            ResultSet rs = conn.createStatement().executeQuery(consulta);
            while (rs.next()) {
                datos[0] = rs.getString("AUTOR");
                datos[1] = rs.getString("TITULO");
                datos[2] = rs.getString("VENTAS");

                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void TablaOnline(DefaultTableModel modelo) {
        Object[] datos = new Object[3];

        String consulta = "SELECT \n" +
                "    a.nombre AS AUTOR, \n" +
                "    l.titulo AS TITULO, \n" +
                "    COUNT(vo.idVenta) AS VENTAS\n" +
                "FROM autores a\n" +
                "JOIN libros l ON a.idAutor = l.idAutor\n" +
                "JOIN ventas_online vo ON l.idLibro = vo.idLibro\n" +
                "GROUP BY a.nombre, l.titulo\n" +
                "ORDER BY VENTAS DESC\n" +
                "LIMIT 3;\n";
        try {
            ResultSet rs = conn.createStatement().executeQuery(consulta);
            while (rs.next()) {
                datos[0] = rs.getString("AUTOR");
                datos[1] = rs.getString("TITULO");
                datos[2] = rs.getString("VENTAS");

                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void informeUno(DefaultTableModel modelo) {
        Object[] datos = new Object[2];

        String consulta = "SELECT e.nombre AS EDITORIAL, COUNT(l.idLibro) AS LIBROS " +
                "FROM editoriales e " +
                "JOIN libros l ON e.idEditorial = l.idEditorial " +
                "GROUP BY e.idEditorial " +
                "ORDER BY LIBROS DESC " +
                "LIMIT 10;";

        try {
            ResultSet rs = conn.createStatement().executeQuery(consulta);
            while (rs.next()) {
                datos[0] = rs.getString("EDITORIAL");
                datos[1] = rs.getInt("LIBROS");

                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void informeDos1(DefaultTableModel modelo) {
        Object[] datos = new Object[2];

        String consulta = "SELECT v.nombre AS VENDEDOR, SUM(vt.precio) AS FACTURACION " +
                "FROM vendedores v " +
                "JOIN ventas_tienda vt ON v.codVendedor = vt.codVendedor " +
                "JOIN estados e ON v.idEstado = e.idEstado " +
                "WHERE e.estado = 'Activo' " +
                "GROUP BY v.codVendedor " +
                "ORDER BY FACTURACION DESC;";

        try {
            ResultSet rs = conn.createStatement().executeQuery(consulta);
            while (rs.next()) {
                datos[0] = rs.getString("VENDEDOR");
                datos[1] = rs.getDouble("FACTURACION");

                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void informeDos2(DefaultTableModel modelo) {
        Object[] datos = new Object[2];

        String consulta = "SELECT p.nombre AS PLATAFORMA, SUM(vo.precio) AS FACTURACION " +
                "FROM plataformas p " +
                "JOIN ventas_online vo ON p.idPlataforma = vo.idPlataforma " +
                "GROUP BY p.idPlataforma " +
                "ORDER BY FACTURACION DESC;";

        try {
            ResultSet rs = conn.createStatement().executeQuery(consulta);
            while (rs.next()) {
                datos[0] = rs.getString("PLATAFORMA");
                datos[1] = rs.getDouble("FACTURACION");

                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

public static void informeTres(DefaultTableModel modelo, int seccion, JLabel jLabel4) {
    Object[] datos = new Object[2];
    int totalVolumenes = 0; // Variable para almacenar la suma

    String consulta = "SELECT u.ubicacion AS UBICACION, COUNT(l.idLibro) AS VOLUMENES " +
            "FROM ubicacion u " +
            "JOIN libros l ON u.ubicacion = l.codUbicacion " +
            "WHERE u.ubicacion LIKE '" + seccion + "%' " +
            "GROUP BY u.ubicacion " +
            "ORDER BY u.ubicacion ASC;";

    try {
        ResultSet rs = conn.createStatement().executeQuery(consulta);
        while (rs.next()) {
            datos[0] = rs.getString("UBICACION");
            int volumenes = rs.getInt("VOLUMENES");
            datos[1] = volumenes;

            totalVolumenes += volumenes; // Sumar los volúmenes

            modelo.addRow(datos);
        }

        // Asignar la suma total al JLabel
        jLabel4.setText(String.valueOf(totalVolumenes));
        
    } catch (SQLException ex) {
        Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
    }
}


    public static void informeCinco(DefaultTableModel modelo) {
        Object[] datos = new Object[2];

        String consulta = "SELECT le.lugar AS CIUDAD, COUNT(l.idlibro) AS LIBRO\n" +
                "FROM lugar_edicion le JOIN libros l\n" +
                "ON le.idLugar = l.idLugar\n" +
                "GROUP BY le.lugar\n" +
                "ORDER BY LIBRO DESC\n" +
                "LIMIT 5;";

        try {
            ResultSet rs = conn.createStatement().executeQuery(consulta);
            while (rs.next()) {
                datos[0] = rs.getString("CIUDAD");
                datos[1] = rs.getDouble("LIBRO");

                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void informeCuatro(DefaultTableModel modelo) {
        Object[] datos = new Object[2];

        String consulta = "SELECT le.ccaa AS 'COMUNIDAD AUTÓNOMA', COUNT(l.idlibro) AS LIBRO\n" +
                "FROM lugar_edicion le JOIN libros l\n" +
                "ON le.idLugar = l.idLugar\n" +
                "GROUP BY le.ccaa\n" +
                "ORDER BY LIBRO DESC\n" +
                "LIMIT 10;";

        try {
            ResultSet rs = conn.createStatement().executeQuery(consulta);
            while (rs.next()) {
                datos[0] = rs.getString("COMUNIDAD AUTÓNOMA");
                datos[1] = rs.getDouble("LIBRO");

                modelo.addRow(datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



}
