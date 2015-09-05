package com.tatsuo.baseballrecorder.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaCodecInfo;
import android.util.AttributeSet;
import android.view.View;

import com.tatsuo.baseballrecorder.R;
import com.tatsuo.baseballrecorder.domain.BattingResult;
import com.tatsuo.baseballrecorder.domain.GameResult;
import com.tatsuo.baseballrecorder.util.Utility;

import java.util.List;

/**
 * Created by tatsuo on 2015/08/28.
 */
public class AnalysisView extends View {

    private Bitmap image = null;
    private List<GameResult> gameResultList = null;

    private static final float[] basePoint = {150.0f, 258.0f};
    private static final float[][] targetPoint = {
            {150.0f, 258.0f},
            {150.0f, 204.0f},
            {150.0f, 248.0f},
            {197.0f, 204.0f},
            {172.0f, 160.0f},
            {103.0f, 204.0f},
            {128.0f, 160.0f},
            {65.0f,  103.0f},
            {150.0f, 73.0f},
            {235.0f, 103.0f},
            {95.0f,  83.0f},
            {205.0f, 83.0f},
            {37.0f,  145.0f},
            {263.0f, 145.0f}
    };
    private static final float[][] targetHomerunPoint = {
            {35.0f,  48.0f},
            {150.0f, 2.0f},
            {265.0f, 48.0f},
            {75.0f,  18.0f},
            {225.0f, 18.0f},
            {10.0f,  122.0f},
            {290.0f, 122.0f}
    };

    private static final int[] lineColor = {
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.BLUE,
            Color.RED,
            Color.RED,
            Color.RED,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY,
            Color.DKGRAY
    };

    public AnalysisView(Context context){
        super(context);
        init(context);
    }

    public AnalysisView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public AnalysisView(Context context, AttributeSet attrs, int defstyle){
        super(context, attrs, defstyle);
        init(context);
    }

    public void init(Context context){
        Resources resources = context.getResources();
        image = BitmapFactory.decodeResource(resources, R.drawable.ground);
    }

    public void setGameResultList(List<GameResult> gameResultList){
        this.gameResultList = gameResultList;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(image == null) {
            return;
        }

        float density = Utility.getDensity();

        Paint basepaint = new Paint();
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);

        canvas.drawBitmap(image, matrix, basepaint);

        if(gameResultList != null){
            for(GameResult gameResult : gameResultList){
                for(BattingResult battingResult : gameResult.getBattingResultList()){
                    int position = battingResult.getPosition();
                    int result = battingResult.getResult();

                    if(position == BattingResult.NON_REGISTED){
                        continue;
                    }

                    Paint paint = new Paint();
                    paint.setColor(lineColor[result]);
                    paint.setStrokeWidth(3f * density);
                    paint.setStrokeCap(Paint.Cap.ROUND);

                    float targetX = targetPoint[position][0];
                    float targetY = targetPoint[position][1];

                    if(result == 10 && position >= 7){
                        targetX = targetHomerunPoint[position-7][0];
                        targetY = targetHomerunPoint[position-7][1];
                    }

                    targetX = targetX + ((float)Math.random()*10 - 5f) * density;
                    targetY = targetY + ((float)Math.random()*10 - 5f) * density;

                    if(targetY < 1.0f){
                        targetY = 1.0f;
                    }

                    canvas.drawLine(basePoint[0]*density, basePoint[1]*density,
                            targetX*density, targetY*density, paint);

                }
            }
        }

    }




}
