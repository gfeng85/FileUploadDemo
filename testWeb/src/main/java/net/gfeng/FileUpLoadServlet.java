package net.gfeng;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet( name = "FileUpLoadServlet",urlPatterns = { "/FileUpLoadServlet"},loadOnStartup = 1)
public class FileUpLoadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUpLoadServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpSession hs = request.getSession();
        System.out.println("doPost");

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            System.out.println("Multipart");
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletContext servletContext = this.getServletConfig().getServletContext();
            File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            factory.setRepository(repository);
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");
            upload.setProgressListener(new ProgressListener(){
                public void update(long pBytesRead, long pContentLength, int pItems) {
                    Map pri = new HashMap();
                    pri.put("itemNum",pItems);
                    pri.put("readSize",pBytesRead);
                    pri.put("totalSize",pContentLength);
                    pri.put("show",pBytesRead+"/"+pContentLength+" byte");
                    pri.put("rate",Math.round(new Float(pBytesRead) / new Float(pContentLength)*100));
                    hs.setAttribute("proInfo", pri);
                }
            });
            try {
                List<FileItem> items = upload.parseRequest(request);
                Iterator<FileItem> iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    //创建文件输出流
                    File file=new File("c:/tmp/testFile");
                    if(!file.exists())
                    {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    FileOutputStream fos = null;
                    InputStream fis = null;
                    try{
                        fos=new FileOutputStream(file);
                        fis= item.getInputStream();
                        long start=System.currentTimeMillis();
                        byte b[]=new byte[4096*1024];
                        int read=fis.read(b);
                        while(read!=-1)
                        {
                            fos.write(b,0,1);
                            read=fis.read(b);
                        }
                        fos.flush();
                        long end=System.currentTimeMillis();
                        System.out.println("timeSpend:"+(end-start)+"ms");

                    }finally{
                        try {fis.close();} catch (Exception e) {}
                        try {fos.close();} catch (Exception e) {}
                    }


                    //打印List中的内容（每一个FileItem的实例代表一个文件，执行这行代码会打印该文件的一些基本属性，文件名，大小等）
                    System.out.println(item);
                }
                response.setContentType("text/json; charset=utf-8");
                PrintWriter out = response.getWriter();
                out.print("{message:success}");
                out.flush();
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
        }
    }
}