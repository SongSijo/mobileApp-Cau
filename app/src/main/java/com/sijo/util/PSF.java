package com.sijo.util;

/**
 * Created by Song on 2015-02-03.
 */


import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HannWindow;


public class PSF implements AudioProcessor {


    float[] audioFloatBuffer;
    //Er zijn evenveel mfccs als er frames zijn!?
    //Per frame zijn er dan CEPSTRA coëficienten
    private float psf;


    private FFT fft;
    private int samplesPerFrame;
    private float sampleRate;

    public PSF(int samplesPerFrame, int sampleRate){
        this.samplesPerFrame = samplesPerFrame;
        this.sampleRate = sampleRate;
        this.fft = new FFT(samplesPerFrame, new HannWindow());

    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        audioFloatBuffer = audioEvent.getFloatBuffer().clone();
//		for (int i = 0; i< audioFloatBuffer.length; i++){
//			System.out.print(Math.round(audioFloatBuffer[i]*100) + " - ");
//		}
        // Magnitude Spectrum
        psf = magnitudeSpectrum(audioFloatBuffer);
        return true;
    }

    @Override
    public void processingFinished() {

    }

    /**
     * computes the magnitude spectrum of the input frame<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param frame Input frame signal
     * @return Magnitude Spectrum array
     */
    public float magnitudeSpectrum(float frame[]){
            float[] amplitude = new float[frame.length];
            float sum=0;

            // calculate FFT for current frame
            fft.forwardTransform(frame);
            // calculate magnitude spectrum
            for (int k = 0; k < frame.length/4; k++){
                amplitude[frame.length/2+k] = fft.modulus(frame, frame.length/2-1-k);
                amplitude[frame.length/2-1-k] = amplitude[frame.length/2+k];
            }

            for(int k=1; k<amplitude.length/2; k++)
            {
            sum += W(sampleRate*k/amplitude.length)*(Math.pow(amplitude[k], (1 / 3.0))- Math.pow(amplitude[k - 1], (1 / 3.0)));
            }

        return sum;
    }

    public float W(float f)
    {
        // Hz to kHz
        f /= 1000;

        float T = (float) ((float) 3.64 / Math.pow(f, 0.8) - 6.5/ Math.exp(0.6 * Math.pow(f - 3.3, 2)) + Math.pow(f, 4)/ Math.pow(10, 3));
        float ECL = T * (1 - 100/125) + 100 + 3;

        return ECL;
    }


    public float getPSF() {
        return psf;
    }

}
