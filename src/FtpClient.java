import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class FtpClient {
    Socket socket;
    InputStream ins;
    OutputStream outs;
    public FtpClient(Socket socket) throws IOException {
        this.socket = socket;
        this.ins=socket.getInputStream();
        this.outs=socket.getOutputStream();
    }

    public void login(String user,String passer) throws Exception{
        byte[] temp0=new byte[512];
        //得到FTP响应
        int size=ins.read(temp0);
        String s=new String(temp0,0,size);
        //输出响应
        System.out.println(s);
        String name="USER "+user+"\r\n";
        //写入登录信息s
        outs.write(name.getBytes());
        outs.flush();
        //服务器返回信息
        size=ins.read(temp0);
        //输出:
        s=new String(temp0,0,size);
        System.out.println(s);
        //密码
        String pass="PASS "+passer+"\r\n";
        //写入密码
        outs.write(pass.getBytes());
        //得到返回信息
        size=ins.read(temp0);
        s=new String(temp0,0,size);
        System.out.println(s);

        System.out.println("Enter 1 to Download,enter 2 to Upload ：");

        Scanner sc2=new Scanner(System.in);
        int Choose=sc2.nextInt();
        if(Choose==1){
            this.download();
        }else if(Choose==2){
            this.upload();
        }else{
            System.out.println("Illegal Input!");

        }

    }
    public Socket getPort() throws Exception{
        //写入FTP模式,取得端口，并链接
        byte[] temp=new byte[1024];
        int size=0;
        String s;
        outs.write("PASV\r\n".getBytes());
        //服务器返回信息
        size=ins.read(temp);
        s=new String(temp,0,size);
        System.out.println(s);

        String[] ports=s.split(",");

        //取得数据传输端口
        String lastport=ports[5].substring(0, (ports[5].length()-4));  //如何取得最后一个数
        int port=Integer.valueOf(ports[4])*256+Integer.valueOf(lastport);

        //链接服务器数据传输端口,获得数据传输的输入输出流
        Socket so=new Socket("127.0.0.1",port);
        return so;
    }
    public void download() throws Exception {
        //写入FTP模式
        byte[] temp=new byte[1024];
        int size=0;
        Socket so=getPort();
        InputStream input=so.getInputStream();
//      OutputStream output=so.getOutputStream();
        //控制台输入下载命令
        Scanner sc3=new Scanner(System.in);
        System.out.println("Enter file name to download ：");
        String name=sc3.nextLine();
        //服务器上的文件
//		File srcfile=new File("E:/upload/"+name);
        //下载到的地方
        File dstfile=new File("E:/download/"+name);
        //判断文件是否存在
//        if (!dstfile.exists()) {
//            System.out.println("File does not exits!");
//            System.exit(0);
//        }
//		FileInputStream fin=new FileInputStream(srcfile);
        FileOutputStream fout=new FileOutputStream(dstfile);

        String downName="RETR "+name+"\r\n";
        outs.write(downName.getBytes());
        outs.flush();
        //服务器返回信息
        size=ins.read(temp);
        //输出:
        String s2=new String(temp,0,size);
        System.out.println(s2);

        while((size=input.read(temp))!=-1){
            fout.write(temp, 0, size);
            fout.flush();
        }

        System.out.println("File has been download in E disk!");
        //写入退出命令
        outs.write("quit".getBytes());
        //读出返回信息
        size=ins.read(temp);
        //打印
        socket.close();
        String quit=new String(temp,0,size);
        System.out.println(quit);

    }
    public void upload() throws Exception{
        //取得服务器的输入输出流
        Socket so=getPort();
        //InputStream input=so.getInputStream();
        OutputStream output=so.getOutputStream();
        Scanner sc4=new Scanner(System.in);
        System.out.println("Enter file name to upload ：");
        String name=sc4.nextLine();
        File file=new File("E:/upload/"+name);
        //判断是否存在该文件
        if (!file.exists()){
            System.out.println("File does not exits!");
            System.exit(0);
        }
        FileInputStream innt=new FileInputStream(file);

        byte[] temp=new byte[1024];
        int size=0;
        String s;
        String uploadName="STOR "+name+"\r\n";
        outs.write(uploadName.getBytes());
        //读出返回信息
        size=ins.read(temp);
        //打印
        s=new String(temp,0,size);
        System.out.println(s);
        while((size=innt.read(temp))!=-1){
            output.write(temp,0,size);
            output.flush();
        }
        output.flush();
        so.close();
        size=ins.read(temp);
        s=new String(temp,0,size);
        System.out.println(s);
        System.out.println("File has been upload in D disk!");

//		写入退出命令
        outs.write("quit\r\n".getBytes());
        //读出返回信息
        size=ins.read(temp);
        //打印

        s=new String(temp,0,size);
        System.out.println(s);

    }
    public static void main(String[] args) {
        Socket socket;
        try {
            socket = new Socket("127.0.0.1",21);
            FtpClient fc=new FtpClient(socket);
            fc.login("chung","jarrychung");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}