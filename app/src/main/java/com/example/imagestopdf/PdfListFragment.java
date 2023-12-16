package com.example.imagestopdf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.imagestopdf.activities.PdfViewActivity;
import com.example.imagestopdf.adapters.AdapterPdf;
import com.example.imagestopdf.models.ModelPdf;

import java.io.File;
import java.util.ArrayList;


public class PdfListFragment extends Fragment {

    private RecyclerView pdfRv;
    Context mContext;

    private ArrayList<ModelPdf> pdfArrayList;
    private AdapterPdf adapterPdf;
    private static final String TAG = "PDF_LIST_TAG";
    private Object modelpdf;


    public PdfListFragment() {
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
        return inflater.inflate(R.layout.fragment_pdf_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        pdfRv = view.findViewById(R.id.pdfRv);

        loadPdfDocuments();
    }

    private void loadPdfDocuments() {

        pdfArrayList = new ArrayList<>();
        adapterPdf = new AdapterPdf(mContext, pdfArrayList, new RVListenerPdf() {
            @Override
            public void onPdfClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder) {

                Intent intent = new Intent(mContext, PdfViewActivity.class);
                intent.putExtra("pdfUri", ""+modelPdf.getUri());
                startActivity(intent);
            }

            @Override
            public void onPdfClick(ModelPdf modelPdf, int position) {
                Intent intent = new Intent(mContext, PdfViewActivity.class);
                intent.putExtra("pdfUri", ""+modelPdf.getUri());
                startActivity(intent);
            }


            @Override
            public void onPdfMoreClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder) {

                showMoreOptions(modelpdf, holder);

            }
        });

        pdfRv.setAdapter(adapterPdf);

        File folder  = new File(mContext.getExternalFilesDir(null),Constants.PDF_FOLDER);

        if(folder.exists()){

            File[] files = folder.listFiles();
            Log.d(TAG,"loadPdfDocuments: Files Count: "+ files.length);

            for(File fileEntry: files){

                Log.d(TAG,"loadPdfDocuments: File Name: "+ fileEntry.getName());

                Uri uri = Uri.fromFile(fileEntry);
                ModelPdf modelPdf = new ModelPdf(fileEntry,uri);
                pdfArrayList.add(modelPdf);
                adapterPdf.notifyItemInserted(pdfArrayList.size());
            }
        }

    }

    private void showMoreOptions(Object modelpdf, AdapterPdf.HolderPdf holder) {

        Log.d(TAG,"ShowMoreOptions: ");

        PopupMenu popupMenu = new PopupMenu(mContext, holder.moreBtn);
        popupMenu.getMenu().add(Menu.NONE,0,0,"Rename");
        popupMenu.getMenu().add(Menu.NONE,1,1,"Delete");
        popupMenu.getMenu().add(Menu.NONE,2,2,"Share");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int itemId = menuItem.getItemId();

                if(itemId == 0)
                {
                    pdfRename((ModelPdf) modelpdf);

                } else if (itemId == 1) {

                    pdfDelete((ModelPdf) modelpdf);
                }
                else if (itemId ==2)
                {

                    pdfShare((ModelPdf) modelpdf);

                }

                return true;
            }
        });

    }


    private void pdfRename(ModelPdf modelpdf)
    {
        Log.d(TAG,"pdfRename: ");

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_rename, null);

        EditText pdfNewNameEt = view.findViewById(R.id.pdfNewNameEt);

        Button renameBtn = view.findViewById(R.id.renameBtn);


        //String previousName = ""+modelpdf.getFile().getName();

        //Log.d(TAG,"pdf: previousName: "+previousName);

        //pdfNewNameEt.setText(previousName);


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);


        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newName = pdfNewNameEt.getText().toString().trim();
                Log.d(TAG,"onClick: newName: "+newName);


                if(newName.isEmpty())
                {

                    Toast.makeText(mContext,"Enter Name....",Toast.LENGTH_SHORT).show();

                }

                else {

                    try {

                        File newFile = new File(mContext.getExternalFilesDir(null), Constants.PDF_FOLDER + "/" + newName + ".pdf");

                        //modelpdf.getFile().renameTo(newFile);
                        Toast.makeText(mContext,"Rename Successfully...",Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG,"renameBtn onClick: ",e);
                        Toast.makeText(mContext,"Failed to rename due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    alertDialog.dismiss();
                }
            }
        });



    }

    private void pdfDelete(ModelPdf modelPdf)
    {


        Log.d (TAG,"pdfDelete: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Files")
                //.setMessage("Are you sure you want to delete the "+modelPdf.getFile().getName())
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {

                            //modelPdf.getFile().delete();
                            //Toast.makeText(mContext,"Deleted Successfully....",Toast.LENGTH_SHORT).show();

                            loadPdfDocuments();

                        }catch (Exception e)
                        {
                            Log.e(TAG, "pdfDelete onClick: ",e);
                            Toast.makeText(mContext,"Failed to delete due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }

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


    private void pdfShare(ModelPdf modelPdf){


        Log.d(TAG,"sharePdf: ");

        //File file = modelPdf.getFile();

        //Uri uri = FileProvider.getUriForFile(mContext, "com.mydomain.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //intent.putExtra(Intent.EXTRA_STREAM,uri);
        startActivity(Intent.createChooser(intent,"Share Pdf"));
    }

}