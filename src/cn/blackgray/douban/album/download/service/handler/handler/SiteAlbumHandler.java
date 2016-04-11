package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * Сվ��ᴦ����
 */
public class SiteAlbumHandler extends AlbumHandler {

	public static final int PAGE_SIZE_IMAGES_SITE = 30;		//Сվ��Ƭ��ҳ��С��һҳ30��ͼ��
	public static final String PAGE_TAG = "start";
	public static final String IMAGE_NAME_REGEX = "p\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://site.douban.com/.+/widget/photos/\\d+/";

	@Override
	public String getURLRegex() {
		return ALBUM_URL_REGEX;
	}

	@Override
	public String getPageRegex() {
		return "/widget/photos/\\d+/\\?\\w+=\\d+";
	}

	@Override
	public boolean removeURLParameter() {
		return true;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE_IMAGES_SITE;
	}

	@Override
	public String getPageTag() {
		return PAGE_TAG;
	}

	@Override
	public String getImageNameRegex() {
		return IMAGE_NAME_REGEX;
	}


	@Override
	public boolean hasRaw() {
		return true;
	}

	@Override
	public String getRawURL(String imageURL) {
		return imageURL.replace("photo/photo", "photo/raw").trim();
	}

	@Override
	public void createBGImage(String source, String pageURL, String imageURL, Map<String, BGImage> map) {

		//			=================��
		//			http://site.douban.com/widget/photos/1803367/
		//			http://site.douban.com/widget/photos/1803367/?start=0
		//			<li>
		//		    <div class="photo-item">
		//		    <a href="http://site.douban.com/widget/photos/1803367/photo/818702905/" title="Dina Larot. Kunst im Foyer (Fernwrme. Wien)" alt="Dina Larot. Kunst im Foyer (Fernwrme. Wien)" class="album_photo" id="p818702905"><img src="http://img3.douban.com/view/photo/thumb/public/p818702905.jpg" ></a>
		//		    <div class="desc">
		//		    
		//		    <p>Dina Larot. Kun...</p>
		//		        <span>5�Ƽ�</span>
		//		    </div>
		//		    </div>
		//			</li>
		//			=================��
		//			http://site.douban.com/108128/widget/photos/7398196/
		//			<a href="http://site.douban.com/108128/widget/photos/7398196/photo/1483981067/" title="Bessa by Voigtlnder
		//	<li>
		//	    <div class="photo-item">
		//	    <a href="http://site.douban.com/127530/widget/photos/5066948/photo/1599127738/" title="һ˲�����ů�ľ���һ����" alt="һ˲�����ů�ľ���һ����" class="album_photo" id="p1599127738"><img src="http://img5.douban.com/view/photo/thumb/public/p1599127738.jpg" ></a>
		//	    <div class="desc">
		//	    
		//	    <p>һ˲�����ů��...</p>
		//	        <a href="http://site.douban.com/127530/widget/photos/5066948/photo/1599127738/#comments">1��Ӧ</a>
		//	    </div>
		//	    </div>
		//	</li>
		
		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		String siteAlbumId = pageURL.substring(pageURL.indexOf("photos/") + 7, pageURL.lastIndexOf("/"));
		//��������
		String startIndexStr = pageURL.substring(0,pageURL.indexOf(siteAlbumId)) + siteAlbumId + "/photo/" + imageId + "/\" title=\"";
		int descStartIndex = source.indexOf(startIndexStr);
		String desc;
		if (descStartIndex != -1) {
			int start = descStartIndex + startIndexStr.length();
			desc = source.substring(start, source.indexOf("\"",start));
		}else{
			desc = "";
		}
		//����Ƭ��������
		//<a href="http://site.douban.com/127530/widget/photos/5066948/photo/1599127738/#comments">1��Ӧ</a>
		String commentTatolStartIndexStr = pageURL.substring(0,pageURL.indexOf(siteAlbumId)) + siteAlbumId + "/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//��3��Ӧ��
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("��Ӧ", ""));
		}
		//����Ƭ��
		//				http://img1.douban.com/view/photo/albumicon/public/p1097123994.jpg
		imageURL = imageURL.replace("thumb", "photo").trim();		//thumb����>photo������ͼ����>��ͼ
		desc = desc.replace("\\t\\n","").trim();
		if (!map.containsKey(imageURL)) {
			BGImage bgImage = new BGImage(desc, imageURL, commentTatol);
			map.put(imageURL, bgImage);
		}

	}


	@Override
	public boolean checkBGImage(BGImage bgImage) {
		return bgImage.getUrl().indexOf("albumicon") < 0;
	}

	@Override
	public String getCommentURL(Album album, BGImage image) {
		//http://site.douban.com/widget/photos/1164317/
		//http://site.douban.com/widget/photos/1164317/photo/1597074577/
		return album.getUrl() + "photo/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		return null;
	}

}
