package com.example.imagestopdf.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imagestopdf.MyApplication;
import com.example.imagestopdf.R;
import com.example.imagestopdf.RVListenerPdf;
import com.example.imagestopdf.models.ModelPdf;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdapterPdf extends RecyclerView.Adapter<AdapterPdf.HolderPdf> {


    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;
    private RVListenerPdf rvListenerPdf;

    private static final String TAG = "ADAPTER_PDF_TAG" ;

    public AdapterPdf(Context context, ArrayList<ModelPdf> pdfArrayList,RVListenerPdf rvListenerPdf) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.rvListenerPdf = rvListenerPdf;
    }

    @NonNull
    @Override
    public HolderPdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_pdf,parent,false);


        return new HolderPdf(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdf holder,  int position) {

        ModelPdf modelPdf = pdfArrayList.get(position);

        String name = modelPdf.getFile().getName();

        long timestamp = modelPdf.getFile().lastModified();

        String formattedDate = MyApplication.formalTimestamp(timestamp);


        loadFileSize(modelPdf, holder);
        loadThumbnailFromPdfFile(modelPdf,holder);

        holder.nameTv.setText(name);
        holder.dateTv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rvListenerPdf.onPdfClick(modelPdf , position);
            }
        });

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rvListenerPdf.onPdfMoreClick(modelPdf,position, holder);
            }
        });


    }

    private void loadThumbnailFromPdfFile(ModelPdf modelPdf, HolderPdf holder) {

        Log.d(TAG,"loadThumbnailFromPdfFile: ");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                Bitmap thumbnailBitmap = null;
                int pageCount = 0;

                try{

                    ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(modelPdf.getFile(),ParcelFileDescriptor.MODE_READ_ONLY);

                    PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);

                    pageCount = pdfRenderer.getPageCount();
                    if(pageCount<=0)
                    {

                    }else{

                        PdfRenderer.Page currentPage = pdfRenderer.openPage(0);

                        thumbnailBitmap = Bitmap.createBitmap(currentPage.getWidth(),currentPage.getHeight(),Bitmap.Config.ARGB_8888);

                        currentPage.render(thumbnailBitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    }

                }catch(Exception e)
                {
                    Log.d(TAG,"loadThumbnailFromPdfFile run: ", e);
                }

                Bitmap finalThumbnailBitmap1 = thumbnailBitmap;
                int finalPageCount = pageCount;

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG,"loadThumbnailFromPdfFile run: Setting thumbnail");

                        Glide.with(context)
                                .load(finalThumbnailBitmap1)
                                .fitCenter()
                                .placeholder(R.drawable.ic_baseline_picture_as_pdf_24)
                                .into(holder.thumbnailTv);

                        holder.pageTv.setText(" "+finalPageCount +" Pages");
                    }
                });
            }
        });

    }

    private void loadFileSize(ModelPdf modelPdf, HolderPdf holder) {

        double bytes = modelPdf.getFile().length();

        double kb = bytes/1024;
        double mb = kb/1024;

        String size = "";

        if(mb >=1){

            size = String.format("%.2f",mb) + " MB";
        }
        else if(kb >=1)
        {
            size = String.format("%.2f",kb) + " KB";
        }
        else{

            size = String.format("%.2f",bytes) + " bytes";
        }

        holder.sizeTv.setText(size);
    }

    @Override
    public int getItemCount() {

        return pdfArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class HolderPdf extends RecyclerView.ViewHolder{

        public ImageView thumbnailTv;
        public TextView nameTv,pageTv,sizeTv,dateTv;
        public ImageButton moreBtn;

        public HolderPdf(@NonNull View itemView) {
            super(itemView);

            thumbnailTv = itemView.findViewById(R.id.thumbnailIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            pageTv = itemView.findViewById(R.id.pagesTv);
            sizeTv = itemView.findViewById(R.id.sizeTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            dateTv = itemView.findViewById(R.id.dateTv);

        }

    }
}
