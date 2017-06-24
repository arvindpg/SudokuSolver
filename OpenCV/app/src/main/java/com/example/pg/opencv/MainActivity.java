package com.example.pg.opencv;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    Button Convert;
    Button Process;

    private static final String TAG = "MainActivity";
    //JavaCameraView javaCameraView;
    //Mat mRgba;
    //Mat imgGray,imgCanny;
    Mat originalImage,sampledImage;
    ProgressBar p;
    private	static	final	int	SELECT_PICTURE	=	1;
    private	String	selectedImagePath;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.iodark_room, menu);
        return true;
    }


    @Override
    public	boolean	onOptionsItemSelected(MenuItem item)	{
        //	Handle	action	bar	item	clicks	here.	The	action	bar	will
        //	automatically	handle	clicks	on	the	Home/Up	button,	so	long
        //	as	you	specify	a	parent	activity	in	AndroidManifest.xml.
        int	id	=	item.getItemId();
        if	(id	==	R.id.action_openGallary)	{
            Intent	intent	=	new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select	Picture"), SELECT_PICTURE);
            return	true;
        }
        return	super.onOptionsItemSelected(item);
    }
    public	void	onActivityResult(int	requestCode,	int	resultCode,	Intent	data)
    {		if	(resultCode	==	RESULT_OK)	{
        if	(requestCode	==	SELECT_PICTURE)	{
            Uri	selectedImageUri	=	data.getData();
            selectedImagePath	=	getPath(selectedImageUri);
            Log.i(TAG,	"selectedImagePath:	"	+	selectedImagePath);
            loadImage(selectedImagePath);
            displayImage(sampledImage);
        }
    }
    }




   // Once	we	have	the	path	ready,	we	call	the	 loadImage() 	method:
    private	void	loadImage(String	path)
    {
        originalImage	=	Highgui.imread(path);
        Mat	rgbImage=new	Mat();

        Imgproc.cvtColor(originalImage,	rgbImage,	Imgproc.COLOR_BGR2RGB);
        Display display	=	getWindowManager().getDefaultDisplay();
        //This	is	"android	graphics	Point"	class
        android.graphics.Point size1;
        size1 = new android.graphics.Point();
        display.getSize(size1);
        int	width	=	(int) size1.x;
        int	height	=	(int)size1.y;
        sampledImage=new	Mat();
        double	downSampleRatio=	calculateSubSampleSize(rgbImage,width,height);
        Imgproc.resize(rgbImage,	sampledImage,	new
                Size(),downSampleRatio,downSampleRatio,Imgproc.INTER_AREA);		try	{
        ExifInterface exif	=	new	ExifInterface(selectedImagePath);
        int	orientation	=	exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                1);
        switch	(orientation)
        {
            case	ExifInterface.ORIENTATION_ROTATE_90:
                //get	the	mirrored	image
                sampledImage=sampledImage.t();
                //flip	on	the	y-axis
                Core.flip(sampledImage,	sampledImage,	1);
                break;
            case	ExifInterface.ORIENTATION_ROTATE_270:
                //get	up	side	down	image
                sampledImage=sampledImage.t();
                //Flip	on	the	x-axis
                Core.flip(sampledImage,	sampledImage,	0);
                break;
        }
    }	catch	(IOException e)	{
        e.printStackTrace();
    }
    }
    private	static	double	calculateSubSampleSize(Mat	srcImage,	int	reqWidth,
                                                          int	reqHeight) {
        //	Raw	height	and	width	of	image
        final int height = srcImage.height();
        final int width = srcImage.width();
        double inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            //	Calculate	ratios	of	requested	height	and	width	to	the	raw
            //height	and	width
            final double heightRatio = (double) reqHeight / (double) height;
            final double widthRatio = (double) reqWidth / (double) width;
            //	Choose	the	smallest	ratio	as	inSampleSize	value,	this	will
            //guarantee	final	image	with	both	dimensions	larger	than	or
            //equal	to	the	requested	height	and	width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    private	void	displayImage(Mat	image)
    {
        //	create	a	bitMap
        Bitmap bitMap	=	Bitmap.createBitmap(image.cols(),
                image.rows(),Bitmap.Config.RGB_565);
        //	convert	to	bitmap:
        Utils.matToBitmap(image,	bitMap);
        //	find	the	imageview	and	draw	it!
        ImageView iv	=	(ImageView)	findViewById(R.id.IODarkRoomImageView);
        iv.setImageBitmap(bitMap);
    }

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    //javaCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }

        }
    };

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCv successfully loaded");

        } else {
            Log.d(TAG, "OpenCV unsuccessfully loaded");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Convert=(Button)findViewById(R.id.convert);


            Convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mat	binaryImage=new	Mat();
                Imgproc.cvtColor(sampledImage,	binaryImage,	Imgproc.COLOR_RGB2GRAY);
                Imgproc.Canny(binaryImage,	binaryImage,80,	100);
                displayImage(binaryImage);

            }
        });
        Process=(Button)findViewById(R.id.Process);
        Process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Processing Image", Toast.LENGTH_SHORT).show();





                        Intent i = new Intent(getApplicationContext(),Main2Activity.class);
                        startActivity(i);


            }
        });

        //javaCameraView = (JavaCameraView) findViewById(R.id.jcv);
        //javaCameraView.setVisibility(SurfaceView.VISIBLE);
        //javaCameraView.setCvCameraViewListener(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
      //  if (javaCameraView != null) javaCameraView.disableView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (javaCameraView != null) javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCv successfully loaded");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        } else {
            Log.d(TAG, "OpenCV unsuccessfully loaded");
            //mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallBack);

        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];
        String sel = MediaStore.Images.Media._ID +"=?";
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, sel, new String[]{id}, null);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String ans= cursor.getString(column_index);

            return ans;
        }
        return uri.getPath();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        //mRgba = new Mat(height, width, CvType.CV_8UC4);
        //imgGray=new Mat(height, width, CvType.CV_8UC1);
        //imgCanny=new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        //mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //mRgba = inputFrame.rgba();
        //Imgproc.cvtColor(mRgba,imgGray,Imgproc.COLOR_RGB2GRAY);
        //Imgproc.Canny(imgGray,imgCanny,50,150);
        //return imgGray;
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
