package com.bawp.coachme.utils;

/**
 * Class: CoachMeGlideModule.java
 *
 * Class the will register the Firebase Storage location into the Glide
 * to allow the download of images from the cloud
 *
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

@GlideModule
public class CoachMeGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());
    }

}
