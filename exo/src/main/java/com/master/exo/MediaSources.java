package com.master.exo;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

public class MediaSources {

    public static MediaSource buildMediaSource(Context context, Uri manifestUri) {
        int type = Util.inferContentType(manifestUri);
        switch (type) {
            case C.TYPE_DASH:
                return newDashMediaSource(context, manifestUri);
            case C.TYPE_SS:
                return newSsMediaSource(context, manifestUri);
            case C.TYPE_HLS:
                return newHlsMediaSource(context, manifestUri);
            case C.TYPE_OTHER:
                return newOtherMediaSource(context, manifestUri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private static MediaSource newDashMediaSource(Context context, Uri manifestUri) {
        DataSource.Factory factory = buildDataSourceFactory(context, new DefaultBandwidthMeter());
        DashChunkSource.Factory factory1 = new DefaultDashChunkSource.Factory(factory);
        DataSource.Factory factory2 = buildDataSourceFactory(context, null);
        return new DashMediaSource.Factory(factory1, factory2)
                .createMediaSource(manifestUri);
    }

    private static MediaSource newSsMediaSource(Context context, Uri manifestUri) {
        DataSource.Factory factory = buildDataSourceFactory(context, new DefaultBandwidthMeter());
        SsChunkSource.Factory factory1 = new DefaultSsChunkSource.Factory(factory);
        DataSource.Factory factory2 = buildDataSourceFactory(context, null);
        return new SsMediaSource.Factory(factory1, factory2)
                .createMediaSource(manifestUri);
    }

    private static MediaSource newHlsMediaSource(Context context, Uri manifestUri) {
        DataSource.Factory factory = buildDataSourceFactory(context, new DefaultBandwidthMeter());
        return new HlsMediaSource.Factory(factory)
                .createMediaSource(manifestUri);
    }

    private static MediaSource newOtherMediaSource(Context context, Uri manifestUri) {
        DataSource.Factory factory = buildDataSourceFactory(context, new DefaultBandwidthMeter());
        return new ExtractorMediaSource.Factory(factory)
                .createMediaSource(manifestUri);
    }

    private static DataSource.Factory buildDataSourceFactory(
            Context context,
            TransferListener<? super DataSource> listener) {
        return new DefaultDataSourceFactory(
                context,
                listener,
                buildHttpDataSourceFactory(listener));
    }

    private static HttpDataSource.Factory buildHttpDataSourceFactory(
            TransferListener<? super DataSource> listener) {
        return new DefaultHttpDataSourceFactory("User-Agent", listener);
    }

}
