package com.example.imagestopdf;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.imagestopdf.adapters.AdapterImage;
import com.example.imagestopdf.models.ModeLIamge;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImagesListFragment extends Fragment {

    private static final String TAG = "IMAGE_LIST_TAG";



    private Uri imageUri = null;

    private FloatingActionButton floatingActionButton;
    private RecyclerView imagesRv;

    private ArrayList<ModeLIamge> allimageArrayList;
    private AdapterImage adapterImage;

    private ProgressDialog progressDialog;



    Context mContext;

    public ImagesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_images_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        floatingActionButton = view.findViewById(R.id.addImageFab);
        imagesRv = view.findViewById(R.id.imagesRv);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadImages();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showInputImageDialog();

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menuu_images,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int itemId = item.getItemId();
        if(itemId == R.id.images_item_delete){

            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(mContext);
            materialAlertDialogBuilder.setTitle("Delete Images")
                                        .setMessage("Are you want to delete All/selected Images")
                    .setPositiveButton("Delete Images", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            deleteImages(true);


                        }
                    })
                    .setNeutralButton("Deleted selected", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            deleteImages(false);
                        }
                    })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    })
                    .show();

        } else if (itemId==R.id.images_item_pdf) {
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(mContext);
            materialAlertDialogBuilder.setTitle("Convert to PDF")
                    .setMessage("Convert All/Selected Images to pdf")
                    .setPositiveButton("CONVERT ALL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            convertImagesToPdf(true);

                        }
                    })
                    .setNeutralButton("CONVERT SELECTED", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            convertImagesToPdf(false);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void convertImagesToPdf(boolean convertAll)
    {
        Log.d(TAG,"convertImagesToPdf: ConvertAll: " + convertAll);

        progressDialog.setMessage("Converting to PDF......");
        progressDialog.show();


        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG,"run: BG work Start......");


                ArrayList<ModeLIamge> imagesToPdfList = new ArrayList<>();
                if(convertAll){
                    imagesToPdfList = allimageArrayList;

                }
                else{

                    for(int i = 0; i<allimageArrayList.size();i++){

                        if(allimageArrayList.get(i).isChecked())
                        {
                            imagesToPdfList.add(allimageArrayList.get(i));
                        }
                    }
                }

                Log.d(TAG,"run: imagesToPdfList Size: "+ imagesToPdfList.size());
                try{

                    File root = new File(mContext.getExternalFilesDir(null),Constants.PDF_FOLDER);
                    root.mkdirs();

                    long timestamp = System.currentTimeMillis();
                    String fileName = "PDF_" +timestamp +".pdf";

                    Log.d(TAG,"run: fileName: "+fileName);

                    File file = new File(root,fileName);

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    PdfDocument pdfDocument = new PdfDocument();

                    for(int i = 0; i<imagesToPdfList.size(); i++){

                        Uri imagesToAdInPdfUri = imagesToPdfList.get(i).getImageUri();

                        try{

                            Bitmap bitmap ;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.getContentResolver(),imagesToAdInPdfUri));
                            }else{

                                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),imagesToAdInPdfUri);
                            }

                            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,false);

                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),i+1).create();
                            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                            Canvas canvas = page.getCanvas();
                            Paint paint = new Paint();
                            paint.setColor(Color.WHITE);

                            canvas.drawPaint(paint);
                            canvas.drawBitmap(bitmap,0f,0f,null);

                            pdfDocument.finishPage(page);
                            bitmap.recycle();

                        }catch(Exception e){

                            Log.e(TAG,"run: ",e);
                        }
                    }

                    pdfDocument.writeTo(fileOutputStream);
                    pdfDocument.close();

                }catch(Exception e)
                {

                    progressDialog.dismiss();
                    Log.d(TAG,"run: ",e);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"run: Converted.........");
                        progressDialog.dismiss();
                        Toast.makeText(mContext,"Converted....",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void deleteImages(boolean deleteAll)
    {
        ArrayList<ModeLIamge> imagesToDeleteList = new ArrayList<>();
        if(deleteAll){

            imagesToDeleteList = allimageArrayList;
        }
        else{

            for(int i = 0; i<allimageArrayList.size();i++)
            {

                if(allimageArrayList.get(i).isChecked()){

                    imagesToDeleteList.add(allimageArrayList.get(i));
                }
            }
        }

        for (int i =0; i<imagesToDeleteList.size();i++){

            try {

                String pathOfImageToDelete = imagesToDeleteList.get(i).getImageUri().getPath();

                File file = new File(pathOfImageToDelete);


                if (file.exists()){
                    boolean isDeleted = file.delete();

                    Log.d(TAG,"DeleteImages: isDeleted: "+isDeleted);

                }

            }catch (Exception e)
            {
                Log.d(TAG,"DeleteImages: ", e);
            }

        }

        Toast.makeText(mContext,"Deleted",Toast.LENGTH_SHORT).show();
        loadImages();

    }

    private void loadImages()
    {
        Log.d(TAG,"loadImages: ");


        allimageArrayList = new ArrayList<>();
        adapterImage = new AdapterImage(mContext,allimageArrayList);

        imagesRv.setAdapter(adapterImage);

        File Folder = new File(mContext.getExternalFilesDir(null),Constants.IMAGES_FOLDER);

        if(Folder.exists())
        {
            Log.d(TAG,"LoadImages: folder exists");
            File[] files = Folder.listFiles();

            if(files != null)
            {
                Log.d(TAG, "loadimages: Folder exists and have images");

                for(File file: files)
                {
                    Log.d(TAG,"loadImages: filename "+file.getName());

                    Uri imagUri = Uri.fromFile(file);
                    ModeLIamge modeLIamge = new ModeLIamge(imagUri,false);
                    allimageArrayList.add(modeLIamge);
                    adapterImage.notifyItemInserted(allimageArrayList.size());
                }
            }
        }

    }

    private void saveImageToAppLevelDirectory(Uri imageUriToBeSaved){

        try {

            Bitmap bitmap;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){

                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.getContentResolver(),imageUriToBeSaved));
            }
            else {

                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),imageUriToBeSaved);
            }

            File directory = new File(mContext.getExternalFilesDir(null),Constants.IMAGES_FOLDER);
            directory.mkdirs();


            long timestamp = System.currentTimeMillis();
            String fileName = timestamp+ ".jpeg";

            File file = new File(mContext.getExternalFilesDir(null),""+ Constants.IMAGES_FOLDER +"/" + fileName);


            try {

                FileOutputStream fileOutputStream  = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90,fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();



                Log.d(TAG, "saveImageAppLevelDirectory: Image Saved");
                Toast.makeText(mContext,"Image Saved" ,Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Log.d(TAG, "saveImageAppLevelDirectory: e");
                Log.d(TAG, "saveImageAppLevelDirectory: Failed to save image due to "+e.getMessage());
                Toast.makeText(mContext,"Failed to Save image due to "+e.getMessage() ,Toast.LENGTH_SHORT).show();
            }


        }

        catch (Exception e)
        {
            Log.d(TAG, "saveImageAppLevelDirectory: e");
            Log.d(TAG, "saveImageAppLevelDirectory: Failed to prepare image due to "+e.getMessage());
            Toast.makeText(mContext,"Failed to prepare image due to "+e.getMessage() ,Toast.LENGTH_SHORT).show();
        }

    }

    private void  showInputImageDialog()
    {

        Log.d(TAG,"showInputImageDialog: ");


        PopupMenu popupMenu = new PopupMenu(mContext,floatingActionButton);

        popupMenu.getMenu().add(Menu.NONE,1,1,"CAMERA");
        popupMenu.getMenu().add(Menu.NONE,2,2,"GALLERY");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int itemId = menuItem.getItemId();
                if(itemId == 1){

                    Log.d(TAG,"onMenuItemClick: Camera is clicked,check if camera permission is granted or not");
                    if(checkCameraPermissions())
                    {
                        pickImageCamera();
                    }
                    else{

                        requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    }
                } else if (itemId == 2) {

                    Log.d(TAG,"onMenuItemClick: Gallery is clicked,check if storage permission is granted or not");
                    if(checkStoragePermission()){

                        pickImageGallery();
                    }
                    else{

                        requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }

                }

                return true;
            }
        });

    }

    private void pickImageGallery()
    {
        Log.d(TAG,"pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){

                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG,"onActivityResult: Picked image gallery: "+imageUri);
                        saveImageToAppLevelDirectory(imageUri);

                        ModeLIamge modeLIamge = new ModeLIamge(imageUri,false);
                        allimageArrayList.add(modeLIamge);
                        adapterImage.notifyItemInserted(allimageArrayList.size());
                    }
                    else {

                        Toast.makeText(mContext,"Cancelled....",Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );


    private void pickImageCamera(){


        Log.d(TAG,"pickImageCamera: ");

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"TEMP IMAGE TITLE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"TEMP IMAGE DESCRIPTION");
        imageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);


    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){

                        Log.d(TAG,"onActivityResult: picked image camera: "+imageUri);
                        saveImageToAppLevelDirectory(imageUri);

                        ModeLIamge modeLIamge = new ModeLIamge(imageUri,false);
                        allimageArrayList.add(modeLIamge);
                        adapterImage.notifyItemInserted(allimageArrayList.size());

                    }else{
                        Toast.makeText(mContext,"Cancelled.....",Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission(){

        Log.d(TAG,"checkStoragePermission: ");
        boolean result = ContextCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;

        return result;
    }


    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {

                    progressDialog.dismiss();
                    Log.d(TAG, "onActivityResult: isGranted: "+isGranted);

                    if(isGranted){
                        pickImageGallery();
                    }
                    else{

                        Toast.makeText(mContext,"Permission denied...",Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private boolean checkCameraPermissions(){

        Log.d(TAG,"checkCameraPermissions: ");
        boolean cameraResult = ContextCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean storageResult = ContextCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;


        return cameraResult && storageResult;

    }



    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    Log.d(TAG,"onActivityResult: ");
                    Log.d(TAG,"onActivityResult: "+result.toString());;

                    boolean areAllGranted = true;

                    for (Boolean isGranted:result.values()){

                        Log.d(TAG,"onActivityResult: isGranted: "+ isGranted);
                        areAllGranted = areAllGranted && isGranted;
                    }


                    if(areAllGranted){
                        Log.d(TAG,"onActivityResult: All Granted e.g. Camera & Storage....");
                        pickImageCamera();
                    }
                    else{

                        Log.d(TAG,"onActivityResult: Camera or Storage or Both denied....");
                        Toast.makeText(mContext,"Camera or Storage or both permission denied....",Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );


}