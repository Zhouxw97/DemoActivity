package Oracle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

/**
 * @Auther: zhouxiaowen
 * @Date: 2019/3/12 15:28
 * @Description:
 */
public class TestOracle {

    private static String USERNAMR = "xcxuser";
    private static String PASSWORD = "Xcxuser123";
    private static String DRVIER = "oracle.jdbc.OracleDriver";
    private static String URL = "jdbc:oracle:thin:@192.168.118.8:1521:orcl";

    Connection connection = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    // ResultSet rs1 = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;


    public Connection getConnection() {
        try {
            Class.forName(DRVIER);
            connection = DriverManager.getConnection(URL, USERNAMR, PASSWORD);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not find !", e);
        } catch (SQLException e) {
            throw new RuntimeException("get connection error!", e);
        }
        return connection;
    }

    public int AddData() throws IOException {
        connection = getConnection();
        String sqlStr = "insert into HETONG_GEJIE_312 (CONTRACT_FILE_ID,CONTRACT_ID,FILE_NAME,FILE_CONTENT,FILE_SIZE) values(?,?,?,?,?)";
        int res = 0;
        try {
            pstm = connection.prepareStatement(sqlStr);
            pstm.setInt(1, 2018000205);
            pstm.setInt(2, 2018000205);
            pstm.setString(3, "1448.pdf");

            File file = new File("D:\\1448.pdf");
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            pstm.setBinaryStream(4,fis,fis.available());
            pstm.setInt(5,378696);

            res = pstm.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            ReleaseResource();
        }
        return res;
    }

    public void ReleaseResource() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstm != null) {
            try {
                pstm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
       /*String selectSql = "SELECT CONTRACT_ID,FILE_NAME from HETONG_GEJIE_312 WHERE ACCOUNT = '28112329'";
        OperateOracle oo=new OperateOracle();
        Connection connection = oo.getConnection();
        PreparedStatement pstm = connection.prepareStatement(selectSql);
        ResultSet rs = pstm.executeQuery();
        while(rs.next()){
            String name = rs.getString(1);
            String phone = rs.getString(2);
            System.out.println(" 姓名："+name+" 手机："+phone);
        }*/
        TestOracle oo=new TestOracle();
        int con = oo.AddData();
        System.out.println(con);
    }

}
