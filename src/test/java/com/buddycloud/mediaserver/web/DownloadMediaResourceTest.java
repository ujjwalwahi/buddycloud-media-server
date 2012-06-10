package com.buddycloud.mediaserver.web;

import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.restlet.resource.ClientResource;

import com.buddycloud.mediaserver.business.model.Media;
import com.buddycloud.mediaserver.commons.Constants;

public class DownloadMediaResourceTest extends MediaResourceTest {
	
	private static final String TEST_OUTPUT_DIR = "test";
	private static final String MEDIA_ID = generateRandomString();

	
	public void testTearDown() throws Exception {
		FileUtils.cleanDirectory(new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + 
				File.separator + BASE_CHANNEL));
		
		dataSource.deleteMedia(MEDIA_ID);
	}
	
	@Override
	protected void testSetUp() throws Exception {
		File destDir = new File(configuration.getProperty(Constants.MEDIA_STORAGE_ROOT_PROPERTY) + File.separator + BASE_CHANNEL);
		if (!destDir.mkdir()) {
			FileUtils.cleanDirectory(destDir);
		}
		
		FileUtils.copyFile(new File(TESTFILE_PATH + TESTMEDIA_NAME), new File(destDir + File.separator + MEDIA_ID));
		
		Media media = buildMedia(MEDIA_ID, TESTFILE_PATH + TESTMEDIA_NAME);
		dataSource.storeMedia(media);
	}
	
	@Test
	public void anonymousSuccessfulDownload() throws Exception {
		ClientResource client = new ClientResource(BASE_URL + "/media/" + BASE_CHANNEL + "/" + MEDIA_ID);
		
		File file = new File(TEST_OUTPUT_DIR + File.separator + "downloaded.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);

		Assert.assertTrue(file.exists());
		
		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
	}
	
	@Test
	public void anonymousPreviewSuccessfulDownload() throws Exception {
		int height = 50;
		int width = 50;
		String url = BASE_URL + "/media/" + BASE_CHANNEL + "/" + MEDIA_ID + "?maxheight=" + height + "&maxwidth=" + width;
		
		ClientResource client = new ClientResource(url);
		
		File file = new File(TEST_OUTPUT_DIR + File.separator + "preview.jpg");
		FileOutputStream outputStream = FileUtils.openOutputStream(file);
		client.get().write(outputStream);

		Assert.assertTrue(file.exists());
		
		// Delete downloaded file
		FileUtils.deleteDirectory(new File(TEST_OUTPUT_DIR));
		outputStream.close();
	}
}
