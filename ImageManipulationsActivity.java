package org.opencv.samples.imagemanipulations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class ImageManipulationsActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
    private static final String  TAG                 = "OCVSample::Activity";

    public static final int      VIEW_MODE_RGBA      = 0;
    public static final int      VIEW_MODE_HIST      = 1;
    public static final int      VIEW_MODE_CANNY     = 2;
    public static final int      VIEW_MODE_SEPIA     = 3;
    public static final int      VIEW_MODE_SOBEL     = 4;
    public static final int      VIEW_MODE_ZOOM      = 5;
    public static final int      VIEW_MODE_PIXELIZE  = 6;
    public static final int      VIEW_MODE_POSTERIZE = 7;
    public static final int		 VIEW_MODE_SAVETMP   = 8;
    public static final int		 VIEW_MODE_FINDTMP   = 9;
    public static final int		 VIEW_MODE_DELETETMP = 10; 

    private MenuItem             mItemPreviewRGBA;
    private MenuItem             mItemPreviewHist;
    private MenuItem             mItemPreviewCanny;
    private MenuItem             mItemPreviewSepia;
    private MenuItem             mItemPreviewSobel;
    private MenuItem             mItemPreviewZoom;
    private MenuItem             mItemPreviewPixelize;
    private MenuItem             mItemPreviewPosterize;
    private MenuItem			 mItemSaveTemp;
    private MenuItem			 mItemSearch4Temp;
    private MenuItem			 mItemDeleteTemp;
   // private SubMenu				 smItemSaveTemp;
    private CameraBridgeViewBase mOpenCvCameraView;

    private Size                 mSize0;

    private Mat                  mIntermediateMat;
    private Mat                  mMat0;
    private MatOfInt             mChannels[];
    private MatOfInt             mHistSize;
    private int                  mHistSizeNum = 25;
    private MatOfFloat           mRanges;
    private Scalar               mColorsRGB[];
    private Scalar               mColorsHue[];
    private Scalar               mWhilte;
    private Point                mP1;
    private Point                mP2;
    private float                mBuff[];
    private Mat                  mSepiaKernel;
    private Mat					 tmpImg; 
    private int					 tmpFlag = 0; 
    public static int           viewMode = VIEW_MODE_RGBA;
    private int 				topX,topY,lowerX,lowerY,countT;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                   
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    

    public ImageManipulationsActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.image_manipulations_surface_view);
        
       
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
        mOpenCvCameraView.setOnTouchListener(ImageManipulationsActivity.this);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
       
        
    }

	@Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
  

        
    
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                               "/sample_picture_" + currentDateandTime + ".jpg";
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;*/
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN:
        	// get the real x y value after offset
            int cols = tmpImg.cols();
            int rows = tmpImg.rows();
            int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
            int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;
            if(countT==0)
            {
            	
            	topX = (int)event.getX()-xOffset;
            	topY = (int)event.getY()-yOffset;
            	countT = countT+1;
            	
            	Log.i(TAG,"onTouch event "+ countT+" topX= "+topX+" topY= "+topY);
            }
            else if(countT==1)
            {
            	lowerX = (int)event.getX()-xOffset;
            	lowerY = (int)event.getY()-yOffset;
            	countT = countT+1;
            	//Core.circle(tmpImg, new Point(lowerX,lowerY), 20,new Scalar(255,0,255));

            	Log.i(TAG,"onTouch event "+ countT+" lowerX= "+lowerX+" lowerY= "+lowerY);
            }	
            else{
            	
            	//do nothing because waiting for processing of template against new image
            }
        	break;
        case MotionEvent.ACTION_UP:
        		v.performClick();
        		break;
        default:
        	 break;
        }
        
        return true;
    }
	
	
	

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
 
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemPreviewRGBA  = menu.add("Preview RGBA");
        mItemPreviewHist  = menu.add("Histograms");
        mItemPreviewCanny = menu.add("Canny");
        mItemPreviewSepia = menu.add("Sepia");
        mItemPreviewSobel = menu.add("Sobel");
        mItemPreviewZoom  = menu.add("Zoom");
        mItemPreviewPixelize  = menu.add("Pixelize");
        mItemPreviewPosterize = menu.add("Posterize");
        //mItemSaveTemp = menu.add("Template Image");
        mItemSaveTemp = menu.add("Select Template");
        mItemSearch4Temp = menu.add("Look For Template");
        mItemDeleteTemp = menu.add("Delete Template");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemPreviewRGBA)
            viewMode = VIEW_MODE_RGBA;
        if (item == mItemPreviewHist)
            viewMode = VIEW_MODE_HIST;
        else if (item == mItemPreviewCanny)
            viewMode = VIEW_MODE_CANNY;
        else if (item == mItemPreviewSepia)
            viewMode = VIEW_MODE_SEPIA;
        else if (item == mItemPreviewSobel)
            viewMode = VIEW_MODE_SOBEL;
        else if (item == mItemPreviewZoom)
            viewMode = VIEW_MODE_ZOOM;
        else if (item == mItemPreviewPixelize)
            viewMode = VIEW_MODE_PIXELIZE;
        else if (item == mItemPreviewPosterize)
            viewMode = VIEW_MODE_POSTERIZE;
        else if (item ==  mItemSaveTemp)
        	viewMode = VIEW_MODE_SAVETMP;
        else if (item == mItemSearch4Temp)
        	viewMode = VIEW_MODE_FINDTMP;
        else if (item == mItemDeleteTemp)
        	viewMode = VIEW_MODE_DELETETMP;
        return true;
    }

    public void onCameraViewStarted(int width, int height) {
    	tmpImg = new Mat(width,height,CvType.CV_32F);
        mIntermediateMat = new Mat();
        mSize0 = new Size();
        mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
        mBuff = new float[mHistSizeNum];
        mHistSize = new MatOfInt(mHistSizeNum);
        mRanges = new MatOfFloat(0f, 256f);
        mMat0  = new Mat();
        mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
        mColorsHue = new Scalar[] {
                new Scalar(255, 0, 0, 255),   new Scalar(255, 60, 0, 255),  new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255),  new Scalar(20, 255, 0, 255),  new Scalar(0, 255, 30, 255),
                new Scalar(0, 255, 85, 255),  new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255),  new Scalar(0, 0, 255, 255),   new Scalar(64, 0, 255, 255),  new Scalar(120, 0, 255, 255),
                new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255),  new Scalar(255, 0, 0, 255)
        };
        mWhilte = Scalar.all(255);
        mP1 = new Point();
        mP2 = new Point();

        // Fill sepia kernel
        mSepiaKernel = new Mat(4, 4, CvType.CV_32F);
        mSepiaKernel.put(0, 0, /* R */0.189f, 0.769f, 0.393f, 0f);
        mSepiaKernel.put(1, 0, /* G */0.168f, 0.686f, 0.349f, 0f);
        mSepiaKernel.put(2, 0, /* B */0.131f, 0.534f, 0.272f, 0f);
        mSepiaKernel.put(3, 0, /* A */0.000f, 0.000f, 0.000f, 1f);
    }

    public void onCameraViewStopped() {
        // Explicitly deallocate Mats
        if (mIntermediateMat != null)
            mIntermediateMat.release();

        mIntermediateMat = null;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        Size sizeRgba = rgba.size();

        Mat rgbaInnerWindow;

        
        int rows = (int) sizeRgba.height;
        int cols = (int) sizeRgba.width;

        int left = cols / 8;
        int top = rows / 8;

        int width = cols * 3 / 4;
        int height = rows * 3 / 4;

        switch (ImageManipulationsActivity.viewMode) {
        case ImageManipulationsActivity.VIEW_MODE_RGBA:
            break;

        case ImageManipulationsActivity.VIEW_MODE_HIST:
            Mat hist = new Mat();
            int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
            if(thikness > 5) thikness = 5;
            int offset = (int) ((sizeRgba.width - (5*mHistSizeNum + 4*10)*thikness)/2);
            // RGB
            for(int c=0; c<3; c++) {
                Imgproc.calcHist(Arrays.asList(rgba), mChannels[c], mMat0, hist, mHistSize, mRanges);
                Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
                hist.get(0, 0, mBuff);
                for(int h=0; h<mHistSizeNum; h++) {
                    mP1.x = mP2.x = offset + (c * (mHistSizeNum + 10) + h) * thikness;
                    mP1.y = sizeRgba.height-1;
                    mP2.y = mP1.y - 2 - (int)mBuff[h];
                    Core.line(rgba, mP1, mP2, mColorsRGB[c], thikness);
                }
            }
            // Value and Hue
            Imgproc.cvtColor(rgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV_FULL);
            // Value
            Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[2], mMat0, hist, mHistSize, mRanges);
            Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
            hist.get(0, 0, mBuff);
            for(int h=0; h<mHistSizeNum; h++) {
                mP1.x = mP2.x = offset + (3 * (mHistSizeNum + 10) + h) * thikness;
                mP1.y = sizeRgba.height-1;
                mP2.y = mP1.y - 2 - (int)mBuff[h];
                Core.line(rgba, mP1, mP2, mWhilte, thikness);
            }
            // Hue
            Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[0], mMat0, hist, mHistSize, mRanges);
            Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
            hist.get(0, 0, mBuff);
            for(int h=0; h<mHistSizeNum; h++) {
                mP1.x = mP2.x = offset + (4 * (mHistSizeNum + 10) + h) * thikness;
                mP1.y = sizeRgba.height-1;
                mP2.y = mP1.y - 2 - (int)mBuff[h];
                Core.line(rgba, mP1, mP2, mColorsHue[h], thikness);
            }
            break;

        case ImageManipulationsActivity.VIEW_MODE_CANNY:
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
            Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
            rgbaInnerWindow.release();
            break;

        case ImageManipulationsActivity.VIEW_MODE_SOBEL:
            Mat gray = inputFrame.gray();
            Mat grayInnerWindow = gray.submat(top, top + height, left, left + width);
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Imgproc.Sobel(grayInnerWindow, mIntermediateMat, CvType.CV_8U, 1, 1);
            Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 10, 0);
            Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
            grayInnerWindow.release();
            rgbaInnerWindow.release();
            break;

        case ImageManipulationsActivity.VIEW_MODE_SEPIA:
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Core.transform(rgbaInnerWindow, rgbaInnerWindow, mSepiaKernel);
            rgbaInnerWindow.release();
            break;

        case ImageManipulationsActivity.VIEW_MODE_ZOOM:
            Mat zoomCorner = rgba.submat(0, rows / 2 - rows / 10, 0, cols / 2 - cols / 10);
            Mat mZoomWindow = rgba.submat(rows / 2 - 9 * rows / 100, rows / 2 + 9 * rows / 100, cols / 2 - 9 * cols / 100, cols / 2 + 9 * cols / 100);
            Imgproc.resize(mZoomWindow, zoomCorner, zoomCorner.size());
            Size wsize = mZoomWindow.size();
            Core.rectangle(mZoomWindow, new Point(1, 1), new Point(wsize.width - 2, wsize.height - 2), new Scalar(255, 0, 0, 255), 2);
            zoomCorner.release();
            mZoomWindow.release();
            break;

        case ImageManipulationsActivity.VIEW_MODE_PIXELIZE:
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Imgproc.resize(rgbaInnerWindow, mIntermediateMat, mSize0, 0.1, 0.1, Imgproc.INTER_NEAREST);
            Imgproc.resize(mIntermediateMat, rgbaInnerWindow, rgbaInnerWindow.size(), 0., 0., Imgproc.INTER_NEAREST);
            rgbaInnerWindow.release();
            break;

        case ImageManipulationsActivity.VIEW_MODE_POSTERIZE:
            /*
            Imgproc.cvtColor(rgbaInnerWindow, mIntermediateMat, Imgproc.COLOR_RGBA2RGB);
            Imgproc.pyrMeanShiftFiltering(mIntermediateMat, mIntermediateMat, 5, 50);
            Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_RGB2RGBA);
            */
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
            rgbaInnerWindow.setTo(new Scalar(0, 0, 0, 255), mIntermediateMat);
            Core.convertScaleAbs(rgbaInnerWindow, mIntermediateMat, 1./16, 0);
            Core.convertScaleAbs(mIntermediateMat, rgbaInnerWindow, 16, 0);
            rgbaInnerWindow.release();
            break;
        case ImageManipulationsActivity.VIEW_MODE_SAVETMP:
        	
        	 /*rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
             Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
             rgbaInnerWindow.setTo(new Scalar(0, 0, 0, 255), mIntermediateMat);
             Core.convertScaleAbs(rgbaInnerWindow, mIntermediateMat, 1./16, 0);
             Core.convertScaleAbs(mIntermediateMat, rgbaInnerWindow, 16, 0);
             rgbaInnerWindow.release();*/
        	//System.out.println("Made to if");
        	if(tmpFlag==0)
        	{
        		//store the current tmpImage as the current frame
        		tmpImg = rgba.clone();
        		
        		//reset count event so know on touch where selecting in template
        		countT = 0;
        
        		tmpFlag = 1; 
        		
        	
        	}
        	else
        	{
        	
        		//get stored tmpImg to do static processing
        		rgba= tmpImg.clone(); 
        		if(countT==2)
        		{
            	    Core.rectangle(rgba,new Point(topY,topX),new Point(lowerY, lowerX),new Scalar(0,0,0));
            	    Core.circle(tmpImg, new Point(topY,topX), 20,new Scalar(255,0,255));
            	    Core.circle(tmpImg,  new Point(lowerY,lowerX), 20,new Scalar(255,0,255));
            	    Log.i(TAG,"Location of Points"+topX+" "+topY+" "+lowerX+" "+lowerY);
            	    Log.i(TAG,"Location of Points"+topX+" "+topY+" "+lowerX+" "+lowerY);

        		}
    	
        		
        	}	
        	break;
        case ImageManipulationsActivity.VIEW_MODE_FINDTMP:
        	Log.i(TAG,"On Find template");
        	///(top, top + height, left, left + width);
         	Log.i(TAG,"Values in Find"+lowerY+" "+topY+" "+lowerX+" "+topX);
        	Mat img_temp = tmpImg.submat(lowerY, lowerY+100,topX, topX+100);
        	Log.i(TAG,"After find template");
        	Log.i(TAG,"Process Image Comparing Template to current image");
        	Log.i(TAG,"Size of Image Template"+img_temp.rows()+" "+img_temp.cols());
         	FeatureDetector detectF = FeatureDetector.create(FeatureDetector.ORB);
    		DescriptorExtractor extractD = DescriptorExtractor.create(DescriptorExtractor.ORB);
    		//initialize matcher
        	DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    		
    		
    		//compute ORB features on image template
        	MatOfKeyPoint keyTemp = new MatOfKeyPoint();
        	Mat descripTemp = new Mat();
        	
        	//for each subwindow comparing to 
        	MatOfKeyPoint keyTest = new MatOfKeyPoint();
        	Mat descripTest = new Mat();
        	
        	//get feature points and descriptors for image template once
    		detectF.detect(img_temp,keyTemp);
    		extractD.compute(img_temp, keyTemp, descripTemp);
        	
    		
    		MatOfDMatch matches = new MatOfDMatch();
    		
    		List<DMatch> matchesList;
    		LinkedList<DMatch> good_matches= new LinkedList<DMatch>();
    	
    		int max_cnt= -1;
    		int curr_cnt = 0; 
    		int curr_j = -1;
    		int curr_i = -1; 
    	   	Log.i(TAG,"Got img_template points");
    		//loop over subwindows of comparison image
        	for(int j = 0; j< rgba.rows()-(img_temp.rows())-1;j =j+(img_temp.rows()/2))
        	{
        		for(int i = 0; i< (rgba.cols()-img_temp.cols())-1;i=i+(img_temp.cols()/2))
        		{
        			
        			Log.i(TAG,"Values of i,j"+i+" ,"+j);
        			Log.i(TAG,"Size of rgba"+rgba.rows()+" "+rgba.cols());
        			Log.i(TAG,"SubMatrix Location "+(i+img_temp.cols()) +" "+(j+img_temp.rows()));
        			detectF.detect(rgba.submat(j,j+img_temp.rows(),i,i+img_temp.cols()),keyTest);
        			extractD.compute(rgba.submat(j,j+img_temp.rows(),i,i+img_temp.cols()), keyTest, descripTest);
        			//get matches from descriptor templates to descriptorTest and store in matches
        			Log.i(TAG,"Before matcher ");
        			matcher.match(descripTemp,descripTest,matches);
        			
        			Log.i(TAG,"After matcher New ");
        			matchesList = matches.toList();
        				
        			Log.i(TAG,"Descriptor Rows "+descripTemp.rows());
        			//good_matches.clear();
        			curr_cnt = 0; 
        			for(int k = 0; k< descripTemp.rows();k++)
        			{
        				Log.i(TAG,"Example Distance "+matchesList.get(k));
        				if(matchesList.get(k).distance<= 20.0)
        				{
        				
        					
        					curr_cnt++;
        					//good_matches.addLast(matchesList.get(k));
        					
        				}
        				
        			}
        			
        			if(curr_cnt>max_cnt)
        			{
        				
        				curr_i = i;
        				curr_j = j; 
        				max_cnt = curr_cnt; 
        	        	

        			}
        			
        		} //end of i
        		
        	}//end of j
        	Log.i(TAG,"Out of Loop i ="+ curr_i+" j = "+curr_j);
        	
        	if(curr_i!=-1 && curr_j!=-1)
        	{
        	detectF.detect(rgba.submat(curr_j,curr_j+img_temp.cols(),curr_i,curr_i+img_temp.rows()),keyTest);
    		extractD.compute(rgba.submat(curr_j,curr_j+img_temp.cols(),curr_i,curr_i+img_temp.rows()), keyTest, descripTest);
    		//get matches from descriptor templates to descriptorTest and store in matches
    		matcher.match(descripTemp,descripTest,matches);
    		
    		matchesList = matches.toList();
    		
    		for(int k = 0; k< descripTemp.rows();k++)
    		{
    			if(matchesList.get(k).distance<= 80.0)
    			{
    		
    				good_matches.addLast(matchesList.get(k));
    				
    			}
    			
    		}
       
    		//got subwindow with most matches now figure out how to modify rgba to show bounding box with most matches
    		 //Mat rgbaInnerWindow = img.submat(0,img_temp.rows(), 0, img_temp.cols());
             //rgbaInnerWindow.setTo(new Scalar(0, 0, 0, 255), img_temp);
             //rgbaInnerWindow.release();
    		
        	
    	    Core.rectangle(rgba,new Point(lowerY,topX),new Point(lowerY+100, topX+100),new Scalar(0,0,0));
            Core.rectangle(rgba,new Point(curr_i,curr_j),new Point(curr_i+img_temp.rows(),curr_j+img_temp.cols()),new Scalar(255,255,0));
        	}
        	
        
        
         
            break;
        	
        case ImageManipulationsActivity.VIEW_MODE_DELETETMP:
        	if(tmpFlag == 1)
        		tmpFlag = 0;
        	break;
        	
        	
        }

        return rgba;
    }
    
    //write a script that compares two acquired images and tries to detect an object
    //one is a subwindows of an image and the other is the template trying to match to
    //this method d
    private List<MatOfDMatch> GetListOfMatches(List<Mat> descripImg, Mat img_temp)
    {
    	List<MatOfDMatch> matchesList=new ArrayList<MatOfDMatch>(); 
    	//compute ORB features on image template
    	FeatureDetector detectF = FeatureDetector.create(FeatureDetector.ORB);
    	MatOfKeyPoint keyTemp = new MatOfKeyPoint();
		detectF.detect(img_temp,keyTemp);
		DescriptorExtractor extractD = DescriptorExtractor.create(DescriptorExtractor.ORB);
		Mat descripTemp = new Mat();
		extractD.compute(img_temp, keyTemp, descripTemp);
    	
		//initialize matcher
    	DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    	
    	//go through list of descriptor matrices for an example image and compute matches with current template
    	//function written this way so don't have to recompute descriptors for image windows over and over again
    	//just compare to a template
    	for(int i = 0; i<descripImg.size();i++){
    		MatOfDMatch tmpM = new MatOfDMatch();
    		matcher.match(descripTemp,descripImg.get(i),tmpM);
    		matchesList.add(tmpM);
    	}
    	
 
    	
    	return matchesList;
    }//end of get list of matches
    
    private Mat processImage(Mat img, Mat img_temp, int winx,int winy, int stepx, int stepy)
    {
    	
    	Log.i(TAG,"Process Image Comparing Template to current image");
     	FeatureDetector detectF = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor extractD = DescriptorExtractor.create(DescriptorExtractor.ORB);
		//initialize matcher
    	DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		
		
		//compute ORB features on image template
    	MatOfKeyPoint keyTemp = new MatOfKeyPoint();
    	Mat descripTemp = new Mat();
    	
    	//for each subwindow comparing to 
    	MatOfKeyPoint keyTest = new MatOfKeyPoint();
    	Mat descripTest = new Mat();
    	
    	//get feature points and descriptors for image template once
		detectF.detect(img_temp,keyTemp);
		extractD.compute(img_temp, keyTemp, descripTemp);
    	
		
		MatOfDMatch matches = new MatOfDMatch();
		
		List<DMatch> matchesList;
		LinkedList<DMatch> good_matches= new LinkedList<DMatch>();
	
		int max_cnt= -1;
		int curr_cnt = 0; 
		int curr_j = -1;
		int curr_i = -1; 
		//loop over subwindows of comparison image
    	for(int i = 0; i< img.rows()-winy;i =i+stepy)
    	{
    		for(int j = 0; j<img.cols()-winx;j=j+stepx)
    		{
    			
    			
    			detectF.detect(img.submat(i,i+winy,j,j+winx),keyTest);
    			extractD.compute(img.submat(i,i+winy,j,j+winx), keyTest, descripTest);
    			//get matches from descriptor templates to descriptorTest and store in matches
    			matcher.match(descripTemp,descripTest,matches);
    			
    			matchesList = matches.toList();
    				
    			//good_matches.clear();
    			curr_cnt = 0; 
    			for(int k = 0; k< descripTemp.rows();k++)
    			{
    				if(matchesList.get(k).distance<= 80.0)
    				{
    					curr_cnt++;
    					//good_matches.addLast(matchesList.get(k));
    					
    				}
    				
    			}
    			
    			if(curr_cnt>max_cnt)
    			{
    				curr_i = i;
    				curr_j = j; 
    				
    			}
    			
    		} //end of j 
    		
    	}//end of i
    	
    	
    		
    	detectF.detect(img.submat(curr_i,curr_i+winy,curr_j,curr_j+winx),keyTest);
		extractD.compute(img.submat(curr_i,curr_i+winy,curr_j,curr_j+winx), keyTest, descripTest);
		//get matches from descriptor templates to descriptorTest and store in matches
		matcher.match(descripTemp,descripTest,matches);
		
		matchesList = matches.toList();
		
		for(int k = 0; k< descripTemp.rows();k++)
		{
			if(matchesList.get(k).distance<= 80.0)
			{
		
				good_matches.addLast(matchesList.get(k));
				
			}
			
		}
   
		//got subwindow with most matches now figure out how to modify rgba to show bounding box with most matches
		 //Mat rgbaInnerWindow = img.submat(0,img_temp.rows(), 0, img_temp.cols());
         //rgbaInnerWindow.setTo(new Scalar(0, 0, 0, 255), img_temp);
         //rgbaInnerWindow.release();
		
        
        Core.rectangle(img,new Point(curr_i,curr_j),new Point(curr_i+winy,curr_j+winx),new Scalar(0,255,0));
		
		
    	return img;
    }
    

}
