package com.sijo.util;

import com.sijo.util.WavFile;
import com.sijo.util.WavFileException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.mfcc.MFCC;


public class FeatureExtractor {
	private String audioFileName;
	private WavFile wavFile;
	private TarsosDSPAudioFormat audioFormat;

    public ArrayList<TarsosDSP_MFCC> tarsosDSP_MFCC_List = new ArrayList<TarsosDSP_MFCC>();
    public ArrayList<TarsosDSP_SPL> tarsosDSP_SPL_List = new ArrayList();


    public class TarsosDSP_SPL
    {
        private double time;
        private double value;

        public TarsosDSP_SPL(double time, double value)
        {
            this.time = time;
            this.value = value;
        }
        public double getValue() {
            return this.value;
        }
        public double getTime() {
            return this.time;
        }
    }


    public class TarsosDSP_MFCC
    {
        private double time;
        private ArrayList<Float> coefficient;

        public TarsosDSP_MFCC(double time, float[] coefficient)
        {
            this.time = time;
            this.coefficient = new ArrayList();
            for (int i = 0; i < coefficient.length; i++)
                this.coefficient.add(Float.valueOf(coefficient[i]));
        }

        public ArrayList<Float> getCoefficient() {
            return this.coefficient;
        }
        public double getTime() {
            return this.time;
        }
    }

    public void precalcTarsosDSP_SoundPressureLevel() throws FileNotFoundException
    {
        int size = 2048;
        int overlap = 0;
        final SilenceDetector silenceDetector = new SilenceDetector();
        AudioDispatcher dispatcher = new AudioDispatcher(createAudioStream(), size, overlap);

        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(new AudioProcessor()
        {

            public void processingFinished() {
                System.out.println("to extract TarsosDSP_SPL from wav is finished");
            }

            public boolean process(AudioEvent audioEvent)
            {
                double time = audioEvent.getTimeStamp();
                double spl = silenceDetector.currentSPL(); // -60이상이면 들린다.
                //System.out.println(time + ";" + spl);
                tarsosDSP_SPL_List.add(new TarsosDSP_SPL(time, spl));
                return true;
            }
        });
        dispatcher.run();
    }


	public FeatureExtractor(String audioFileName) throws IOException, WavFileException {
		this.audioFileName = audioFileName;
		InputStream is = new BufferedInputStream(new FileInputStream(new File(audioFileName)));
		this.wavFile = WavFile.openWavFile(is);
		this.audioFormat = new TarsosDSPAudioFormat(wavFile.getSampleRate(),
				wavFile.getValidBits(), wavFile.getNumChannels(), true, false);
	}

	private UniversalAudioInputStream createAudioStream() throws FileNotFoundException {
		InputStream is = new BufferedInputStream(new FileInputStream(new File(audioFileName)));
		return new UniversalAudioInputStream(is, audioFormat);
	}


    /*public ArrayList<TarsosDSP_MFCC> getTarsosDSP_MFCC_List(){
        if(!tarsosDSP_MFCC_flag)
            try {
                precalcTarsosDSP_MFCC();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        return this.tarsosDSP_MFCC_List;
    }*/

	public void precalcTarsosDSP_MFCC() throws FileNotFoundException {

		int sampleRate = 44100;
		int size = 2048;
		int overlap = 0;
		final AudioDispatcher dispatcher = new AudioDispatcher(createAudioStream(), size, overlap);
		final MFCC mfcc = new MFCC(size, sampleRate, 50, 50, 200, 3000);
		dispatcher.addAudioProcessor(mfcc);
		dispatcher.addAudioProcessor(new AudioProcessor() {

			@Override
			public void processingFinished() {
                //System.out.println("to extract TarsosDSP_MFCC from wav is finished");
			}

			@Override
			public boolean process(AudioEvent audioEvent) {
                float []floatBuffer;
                floatBuffer = mfcc.getMFCC();
                tarsosDSP_MFCC_List.add(new TarsosDSP_MFCC(audioEvent.getTimeStamp(), floatBuffer));
                return true;
			}
		});
		dispatcher.run();
	}

}
