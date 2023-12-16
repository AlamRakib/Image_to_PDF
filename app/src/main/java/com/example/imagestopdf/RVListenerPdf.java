package com.example.imagestopdf;

import com.example.imagestopdf.adapters.AdapterPdf;
import com.example.imagestopdf.models.ModelPdf;

public interface RVListenerPdf {

    void onPdfClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder);

    void onPdfClick(ModelPdf modelPdf, int position);
    void onPdfMoreClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder);

}
