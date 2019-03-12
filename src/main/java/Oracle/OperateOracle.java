package Oracle;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: zhouxiaowen
 * @Date: 2019/3/12 14:23
 * @Description:
 */
public class OperateOracle {

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

    public Connection AddData(String stuName, int gender, int age, String address) throws IOException {
        connection = getConnection();
        String sql = "select count(*) from student where 1 = 1";
        String sqlStr = "insert into HETONG_GEJIE_312 (id,stu_name,gender,age,address,note1) values(?,?,?,?,?,?)";
        int count = 0;
        try {
            pstm = connection.prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1) + 1;
                System.out.println(rs.getInt(1));
            }
            pstm = connection.prepareStatement(sqlStr);
            pstm.setInt(1, count);
            pstm.setString(2, stuName);
            pstm.setInt(3, gender);
            pstm.setInt(4, age);
            pstm.setString(5, address);

            File file = new File("://export//test.txt");
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            //InputStream fis = new FileInputStream(infile);
            pstm.setBinaryStream(6,fis,fis.available());
            pstm.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            ReleaseResource();
        }
        return connection;
    }

    public void DeleteData(String stuName) {
        connection = getConnection();
        String sqlStr = "delete from student where stu_name=?";
        System.out.println(stuName);
        try {
            pstm = connection.prepareStatement(sqlStr);
            pstm.setString(1, stuName);
            pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ReleaseResource();
        }
    }

    public void UpdateData(String path) throws IOException {
        connection = getConnection();
        String selectSql1 = "select contract_id,SUB_REFERENCE from T_CONTRACT_TMP where contract_id is not null and SUB_REFERENCE is not null";
        String sqlStr = "update student set note1=? where id=?";
        String selectSql2 = "select FILENAME,FILE_URL,CREATEDATE,ATTACHMENT_SIZE from CTP_ATTACHMENT@V3LINK where SUB_REFERENCE = ?";
        // String insertSql1 = "insert into T_CONTRACT_FILE_CONTENT_TMP (contract_file_id,contract_id,file_name,file_content,file_size) values(S_T_FILE.Nextval,?,?,?,?)";
        String insertSql3 = "insert into T_CONTRACT_FILE_ATTCH_TMP (contract_file_id,contract_id,file_name,file_content,file_size) values(S_T_FILE2.Nextval,?,?,?,?)";

        try {
            pstm = connection.prepareStatement(selectSql1);
            rs = pstm.executeQuery();
            while (rs.next()) {
                int contract_id = rs.getInt("contract_id");
                long SUB_REFERENCE = rs.getLong("SUB_REFERENCE");
                PreparedStatement pstm1 = null;
                ResultSet rs1 = null;
                try{
                    pstm1 = connection.prepareStatement(selectSql2);
                    pstm1.setLong(1,SUB_REFERENCE);
                    rs1 = pstm1.executeQuery();
                    while (rs1.next()) {
                        String fileName=rs1.getString("FILENAME");
                        long FILE_URL=rs1.getLong("FILE_URL");
                        Date CREATEDATE=rs1.getDate("CREATEDATE");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String time = sdf.format(CREATEDATE);
                        String[] day = time.split("-");
                        int ATTACHMENT_SIZE=rs1.getInt("ATTACHMENT_SIZE");

                        FileInputStream fis = null;
                        PreparedStatement pstmFile = null;
                        try{
                            String filePath  = path+"\\"+day[0]+"\\"+day[1]+"\\"+day[2]+"\\"+FILE_URL;
                            File file = new File(filePath);
                            if(file.exists()){
                                fis = new FileInputStream(file);
                                pstmFile = connection.prepareStatement(insertSql3);
                                pstmFile.setInt(1,contract_id);
                                pstmFile.setString(2,fileName);
                                pstmFile.setBinaryStream(3,fis,fis.available());
                                pstmFile.setInt(4,ATTACHMENT_SIZE);
                                pstmFile.executeUpdate();

                                connection.commit();
                            }else{
                                System.out.println("======file not exist=======" + filePath);
                            }
                        }catch (Exception e) {
                            throw e;
                        }finally{
                            if(fis != null){
                                fis.close();
                            }
                            if(pstmFile != null){
                                pstmFile.close();
                            }
                        }
                    }
                }catch(Exception e){
                    throw e;
                }finally{
                    if(rs1 != null){
                        rs1.close();
                    }
                    if(pstm1 != null){
                        pstm1.close();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            ReleaseResource();
        }

    }



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

    public static void main(String[] args) throws IOException {
        String path = "F:\\update";
        OperateOracle oo=new OperateOracle();
        oo.UpdateData(path);

    }


}