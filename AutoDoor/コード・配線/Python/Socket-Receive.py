import RPi.GPIO as GPIO
import time
import sys
import main
import socket #UDP送信
import struct #数値→バイト列変換用
from contextlib import closing #with用

SWITCH_PIN = 5 # タクトスイッチのピン番号(BCM番号)
flag = 0
a = 1.0
power = 0
p = 0.0
count = 0

UDP_IP = "" #このままでいい
UDP_PORT =60000 #ポート番号
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) #ソケットの生成
sock.bind((UDP_IP, UDP_PORT)) #ソケットを登録する


with closing(sock): #プログラム終了時にソケットを自動的に閉じる
    d = main.ta7291(18, 24, 25)
    d.drive(power)
    GPIO.setup(SWITCH_PIN, GPIO.IN, pull_up_down=GPIO.PUD_DOWN) # プルダウン
    while True: #無限ループ
        try:
            if GPIO.input(SWITCH_PIN) == 1:
                a = 1.0

            data, addr = sock.recvfrom(1024) #受信する
            #time.sleep(3)
            p = str( struct.unpack('>d' , data)[0] )
            print(p)
            if( p == "1.0" or a == 1.0):
                while( flag == 0 ):
                    print("Sensor on Move(OPEN)")
                    power = 1
                    while power != 0: #無限ループ
                        if GPIO.input(SWITCH_PIN) == 1:
                            print("SWITCH ON")
                            flag = 1
                            #print(flag)
                            power = 0

                        d.drive(power)
                    time.sleep(1)
                while( flag == 1 ):
                    print("5 after Move(CLOSE)")
                    power = -1
                    while power != 0: #無限ループ
                        if GPIO.input(SWITCH_PIN) == 1:
                            print("SWITCH ON")
                            flag = 0
                            power = 0
                        d.drive(power)
                p = 0.0
            count = 1 - count
            a = 0.0
            time.sleep(0.1)
        except KeyboardInterrupt:
            d.cleanup()
            exit()
