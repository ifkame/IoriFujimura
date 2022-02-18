#!/usr/bin/env python
# -*- coding: utf-8 -*-

def reading(sensor):
    import time
    import RPi.GPIO as GPIO
    
    #以前に実行したGPIOポートの状態が残っていた時に
    #出力される警告文を抑制する
    GPIO.setwarnings(False)

    #PINの準備
    GPIO.setmode(GPIO.BCM)
    TRIG = 23    #超音波の送信PIN
    ECHO = 24    #超音波を受信PIN

    if sensor == 0:
        #PINの設定
        GPIO.setup(TRIG, GPIO.OUT)
        GPIO.setup(ECHO, GPIO.IN)
        GPIO.output(TRIG, GPIO.LOW)
        time.sleep(0.3)

        #超音波の送信
        GPIO.output(TRIG, True)
        time.sleep(0.00001)
        GPIO.output(TRIG, False)

        #超音波が帰って来ない間の時間を取得
        while GPIO.input(ECHO) == 0:
            signaloff = time.time()

        #超音波が帰って来ない間の時間を取得
        while GPIO.input(ECHO) == 1:
            signalon = time.time()

        timepassed = signalon - signaloff   #超音波の到達時間
        distance = timepassed * 17000       #超音波の到達時間で距離を計算
        return distance
        GPIO.cleanup()
    else:
        print ("Incorrect usonic() function varible.")


print (reading(0))
