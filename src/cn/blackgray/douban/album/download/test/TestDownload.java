package cn.blackgray.douban.album.download.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestDownload {

	public static void main(String[] args) {
		try {
			URL url = new URL("http://img3.douban.com/view/photo/photo/public/p1601592092.png");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(false);
			
			conn.setConnectTimeout(10*1000);	//�������ӳ�ʱ
			conn.setReadTimeout(10*1000);		//���ö�ȡ��ʱ
			conn.setDoInput(true);				//Ĭ��Ϊtrue
			conn.connect();
			System.out.println("��getContentLength��" + conn.getContentLength());
			System.out.println("��getContentType��" + conn.getContentType());
			InputStream in = conn.getInputStream();
			
			
			
			BufferedInputStream inputStream = new BufferedInputStream(in);
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File("d://TEST.JPG")));
			byte[] data = new byte[1024];
			int n = 0; 
			while ((n = inputStream.read(data)) != -1) {
				outputStream.write(data,0,n);
			}
			outputStream.flush();
			in.close();
			inputStream.close();
			outputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}