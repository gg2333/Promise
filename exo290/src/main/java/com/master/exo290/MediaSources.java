package com.master.exo290;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

public class MediaSources {

    public static MediaSource buildMediaSource(Context context, Uri uri) {
        return buildMediaSource(context, uri, null);
    }

    public static MediaSource buildMediaSource(Context context, Uri uri, String overrideExtension) {
        int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return newDashMediaSource(context, uri);
            case C.TYPE_SS:
                return newSsMediaSource(context, uri);
            case C.TYPE_HLS:
                return newHlsMediaSource(context, uri);
            case C.TYPE_OTHER:
                return newOtherMediaSource(context, uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private static MediaSource newDashMediaSource(Context context, Uri manifestUri) {
        DataSource.Factory factory = buildDataSourceFactory(context);
        return new DashMediaSource.Factory(factory)
                .createMediaSource(manifestUri);
    }

    private static MediaSource newSsMediaSource(Context context, Uri manifestUri) {
        DataSource.Factory factory = buildDataSourceFactory(context);
        return new SsMediaSource.Factory(factory)
                .createMediaSource(manifestUri);
    }

    private static MediaSource newHlsMediaSource(Context context, Uri manifestUri) {
        DataSource.Factory factory = buildDataSourceFactory(context);
        return new HlsMediaSource.Factory(factory)
                .createMediaSource(manifestUri);
    }

    private static MediaSource newOtherMediaSource(Context context, Uri manifestUri) {
        DataSource.Factory factory = buildDataSourceFactory(context);
        return new ExtractorMediaSource.Factory(factory)
                .createMediaSource(manifestUri);
    }

    private static DataSource.Factory buildDataSourceFactory(Context context) {
        return new DefaultDataSourceFactory(context, buildHttpDataSourceFactory());
    }

    private static HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSourceFactory("User-Agent");
    }

}
