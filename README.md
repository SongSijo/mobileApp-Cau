# Voice Factory


# Hermes


 * TarsosDSP와 PSF 특징을 활용할 수 있도록 한다.

 
# Feature Extractor


 * Wave file 을 이용하여 Feature를 추출한다.
```javascript
FeatureExtractor featureExtractor = new FeatureExtractor(destinationPath);
featureExtractor.precalcTarsosDSP_MFCC(); // MFCC 값 추출
featureExtractor.precalcTarsosDSP_SPL(); // MFCC 값 추출
```

# DistanceCal

 * Wave File을 이용하여 거리를 구할 수 있는 파일이다.


# PSF

 * PSF를 구할 수 있다.

## Change log:

###### 2015-12-09
* 전체적인 프로젝트 파일 업로드.
