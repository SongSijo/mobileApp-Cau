package com.sijo;

import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.sijo.util.DistanceCal;
import com.sijo.util.ExtAudioRecorder;
import com.sijo.util.FeatureExtractor;
import com.sijo.util.FeatureExtractor.TarsosDSP_MFCC;
import com.sijo.util.FeatureExtractor.TarsosDSP_SPL;
import com.sijo.util.WavFile;
import com.sijo.util.WavFileException;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab1 extends Fragment {

    private MediaPlayer backgroundMusic;
    Drawable ready;
    Drawable recording;
    Drawable[] picture;

    TextView tv1;
    TextView tv2;
    TextView first_text;
    TextView second_text;

    public ArrayList<TarsosDSP_MFCC> tar1 = new ArrayList<TarsosDSP_MFCC>();
    public ArrayList<TarsosDSP_MFCC> tar_c1 = new ArrayList<TarsosDSP_MFCC>();


    private ExtAudioRecorder extAR = null;
    private ExtAudioRecorder extCF = null;
    String fRecord;
    String[] fConfirm;
    int a = 0;
    float firstSimilar = 100;
    float secondSimilar = 100;
    int firstSimilarN = 100;
    int secondSimilarN = 100;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);


        //레코드 선언 + 레코드 저장 위치를 지정한다.
        extAR = ExtAudioRecorder.getInstanse(false);

        //Jinx 폴더를 만들어주고 wav 파일을 쓰고 읽을 수 있도록 한다.
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vo_Factory/");
        file.mkdir();

        fRecord = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vo_Factory/record.wav";
        fConfirm = new String[12];

        for(int i=0; i<fConfirm.length;i++)
            fConfirm[i] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vo_Factory/" + Integer.toString(i + 1) + ".wav";
        extAR.setOutputFile(fRecord);


        final ImageButton record;
        final ImageButton first;
        final ImageButton second;

        //이미지 세팅
        ready = getResources().getDrawable(R.drawable.record);
        recording = getResources().getDrawable(R.drawable.record_ing);
        picture = new Drawable[12];
        picture[0] = getResources().getDrawable(R.drawable.p1);
        picture[1] = getResources().getDrawable(R.drawable.p2);
        picture[2] = getResources().getDrawable(R.drawable.p3);
        picture[3] = getResources().getDrawable(R.drawable.p4);
        picture[4] = getResources().getDrawable(R.drawable.p5);
        picture[5] = getResources().getDrawable(R.drawable.p6);
        picture[6] = getResources().getDrawable(R.drawable.p7);
        picture[7] = getResources().getDrawable(R.drawable.p8);
        picture[8] = getResources().getDrawable(R.drawable.p9);
        picture[9] = getResources().getDrawable(R.drawable.p10);
        picture[10] = getResources().getDrawable(R.drawable.p11);
        picture[11] = getResources().getDrawable(R.drawable.p12);


        //음악 녹음 or Not

        tv1 = (TextView) v.findViewById(R.id.ready_text);
        tv2 = (TextView) v.findViewById(R.id.record_text);
        first_text = (TextView) v.findViewById(R.id.first_text);
        second_text = (TextView) v.findViewById(R.id.second_text);
        record = (ImageButton) v.findViewById(R.id.record_start);
        first = (ImageButton) v.findViewById(R.id.first);
        second = (ImageButton) v.findViewById(R.id.second);


        tv1.setPaintFlags(tv1.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG | Paint.UNDERLINE_TEXT_FLAG);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a++;
                if (a % 2 == 1) {
                    //TextView 속성 변경
                    tv1.setPaintFlags(tv1.getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG & ~Paint.UNDERLINE_TEXT_FLAG);
                    tv2.setPaintFlags(tv2.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG | Paint.UNDERLINE_TEXT_FLAG);

                    //Record부분
                    record.setImageDrawable(recording);
                    extAR.prepare();
                    extAR.start();

                    //음악 꺼지고
                } else {
                    //TextView 속성 변경
                    tv2.setPaintFlags(tv2.getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG & ~Paint.UNDERLINE_TEXT_FLAG);
                    tv1.setPaintFlags(tv1.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG | Paint.UNDERLINE_TEXT_FLAG);

                    //Record부분
                    record.setImageDrawable(ready);
                    extAR.stop();
                    extAR.release();
                    extAR = ExtAudioRecorder.getInstanse(false);
                    extAR.setOutputFile(fRecord);


                    try {
                        FeatureExtractor temp = new FeatureExtractor(fRecord);
                        FeatureExtractor efex1;

                        temp.precalcTarsosDSP_SoundPressureLevel();
                        ArrayList<TarsosDSP_SPL> splL = temp.tarsosDSP_SPL_List;

                        double[] chunk = new double[2];
                        int flag = 0;
                        for (int i = 2; i < splL.size(); i++) {
                            double task = splL.get(i).getValue();
                            if (task > -60 && flag == 0) {
                                flag++;
                                chunk[0] = splL.get(i).getTime();
                            }

                            if (task < -70 && flag == 1) {
                                flag++;
                                chunk[1] = splL.get(i).getTime();
                            }

                        }
                        if (chunk[1] == 0) {
                            chunk[1] = splL.get(splL.size() - 1).getTime();
                            WavFile.chunk(fRecord, chunk[0] - 0.2, chunk[1]);
                        } else
                            WavFile.chunk(fRecord, chunk[0] - 0.2, chunk[1]);

                        //File cropFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vo_Factory/record_crop.wav");
                        //cropFile.delete();

/*
                        if(chunk[0] > 0.3)
                        else

*/
                        backgroundMusic = MediaPlayer.create(v.getContext(), Uri.parse(fRecord.substring(0, fRecord.length() - 4) + "_crop.wav"));
                        backgroundMusic.setLooping(false);
                        backgroundMusic.start();
                        FeatureExtractor fex = new FeatureExtractor(fRecord.substring(0, fRecord.length() - 4) + "_crop.wav");
                        fex.precalcTarsosDSP_MFCC();
                        tar1.addAll(fex.tarsosDSP_MFCC_List);

                        float k = 999999999;

                        for (int j = 0; j < fConfirm.length; j++) {
                            tar_c1.clear();
                            efex1 = new FeatureExtractor(fConfirm[j]);
                            efex1.precalcTarsosDSP_MFCC();
                            tar_c1.addAll(efex1.tarsosDSP_MFCC_List);
                            DistanceCal discal1 = new DistanceCal(tar1, tar_c1);
                            if (discal1.getSimilar() < k) {
                                k = discal1.getSimilar();

                                //Similar Number 구하기
                                secondSimilarN = firstSimilarN;
                                firstSimilarN = j;


                                //Similar 구하기
                                secondSimilar = firstSimilar;
                                if (k < 10000)
                                    firstSimilar = 100;
                                else if (k < 100000)
                                    firstSimilar = (100000 - k) / 1000;
                                else if (k > 100000)
                                    firstSimilar = 0;

                            }
                        }

                        for (int p = 0; p < 12; p++) {
                            if (firstSimilarN == p) {
                                first.setImageDrawable(picture[p]);
                            }
                            if (secondSimilarN == p) {
                                second.setImageDrawable(picture[p]);
                            }
                        }
                        first_text.setText(Float.toString(firstSimilar));
                        second_text.setText(Float.toString(secondSimilar));
                        tar1.clear();
                        tar_c1.clear();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WavFileException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a!=0) {
                    backgroundMusic = MediaPlayer.create(v.getContext(), Uri.parse(fConfirm[firstSimilarN]));
                    backgroundMusic.setLooping(false);
                    backgroundMusic.start();
                }
            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a!=0) {
                    backgroundMusic = MediaPlayer.create(v.getContext(), Uri.parse(fConfirm[secondSimilarN]));
                    backgroundMusic.setLooping(false);
                    backgroundMusic.start();
                }
            }
        });
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

}
