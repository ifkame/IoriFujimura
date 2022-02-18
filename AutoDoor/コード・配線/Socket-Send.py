#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import socket #UDP送信
import time #待機時間用
import struct #数値→バイト列変換用
from contextlib import closing #with用
import ipaddress #入力IPアドレスの形式確認用

# host = '169.254.169.5' # IPアドレス（変更する！）
port = 60000 # ポート番号
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) #ソケットの設定
LED_PIN = 17 # LEDピン番号(BCMの番号)
SWITCH_PIN = 27 # タクトスイッチのピン番号(BCM番号)
human_pin = 13 # Sensor

distinct = False
list_A = ["no1","no2"]
count = 0
data = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
i = 0
index = -1

#GPIOの設定
GPIO.setmode(GPIO.BCM) #GPIOのモードを"GPIO.BCM"に設定
#GPIO23を入力モードに設定してプルダウン抵抗を有効にする
GPIO.setup(SWITCH_PIN, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
GPIO.setup(human_pin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
GPIO.setup(LED_PIN, GPIO.OUT)

#IPアドレスの入力関係
print("Destination IP address:")
while True:
    try:
        print(">",end="") #>を改行無しで表示
        inputip = input() #入力させる
        ipaddress.ip_address(inputip) #入力が誤った形式だとエラーを吐く
    except KeyboardInterrupt:
        exit() #Ctrl+Cが入力されたらプログラムを抜ける
    except:
        print("Incorrect IP address. input IP address again.(xxx.xxx.xxx.xxx)")
    else:
        break #正しいIPアドレスだったらwhileを抜ける

with closing(sock): #プログラム終了時にソケットを自動的に閉じる
    while True: #無限ループ
        try:
            if list_A[count] == list_A[1-count]:
                distinct = True
            else:
                distinct = False
            
            if GPIO.input(SWITCH_PIN) == 1 or GPIO.input(human_pin) == 1:
                if distinct == False:
                    #if(d == 1.0):
                    #   sock.shutdown(socket.SHUT_WR)
                    print("pin status:", GPIO.input(SWITCH_PIN),", ", GPIO.input(human_pin))
                    data[i] = 1.0 #適当な数値
                    print("LED ON")
                    GPIO.output(LED_PIN, GPIO.HIGH)
                    list_A[count] = -1
            elif GPIO.input(SWITCH_PIN) == 0 and GPIO.input(human_pin) == 0:
                print("pin status:", GPIO.input(SWITCH_PIN),", ", GPIO.input(human_pin))
                data[i] = 0.0 #適当な数値
                print("LED OFF")
                GPIO.output(LED_PIN, GPIO.LOW)
                # time.sleep(1)
                list_A[count] = -1
            
            if i == 9:
                data.reverse()
                if 1.0 in data:
                    index = data.index(1.0)
                data.reverse()
                print(data, "  ", len(data)-(index+1))

                for put in range(0, i+1):
                    if index != -1:
                        print(put+1, "番目のデータ ", data[put])
                        if len(data)-(index+1) == put:
                            print(put+1, "番目のデータ ", data[put])
                            ds = struct.pack('>d', data[put] ) #ビッグエンディアンのバイト列に変換
                            print("send: ", ds) #送信したバイト列を送信側に表示
                            sock.sendto(ds, (inputip, port)) #ソケットにUDP送信
                            time.sleep(10) #10秒待機
                data = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
            
            distinct = False
            index = -1
            list_A[count] = data[i]
            count = 1 - count
            i = (i + 1) % 10
            time.sleep(0.1) #1秒待機
        except KeyboardInterrupt:
            GPIO.cleanup()
            exit() #Ctrl+Cが入力されたらプログラムを抜ける
