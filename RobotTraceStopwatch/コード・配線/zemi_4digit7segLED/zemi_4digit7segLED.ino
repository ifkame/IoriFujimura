const int dig1 = 1;
const int dig2 = 2;
const int dig3 = 3;
const int dig4 = 4;
 
const int a = 12;
const int b = 10;
const int c =  8;
const int d =  6;
const int e =  5;
const int f = 11;
const int g =  9;
const int dp = 7;

//↓追加：配列に入れてまとめて作業できるようにする
const int alldig[] = {dig1, dig2, dig3, dig4};
const int alllight[] = {a, b, c, d, e, f, g, dp};

int digitsize = sizeof(alldig)/sizeof(alldig[0]);
int segsize = sizeof(alllight)/sizeof(alllight[0]);

// 全てのDIG(桁)をクリア
void clearDigit(){
  for(int i = 0; i < digitsize; i++){
    digitalWrite(alldig[i],HIGH);
  }  
}
// 全てのセグ(A-G,DP)をクリア
void clearSegment(){
  for(int i = 0; i < segsize-1; i++){
    digitalWrite(alllight[i],LOW);
  }
}
 
void setup() {
  Serial.begin(115200);
  // 全てのピンを出力にする
  for(int i =0; i < 13; i++){
    pinMode((i+1),OUTPUT);
  }
  
  // 全てのDIG(桁)をクリア
  clearDigit();
  
  // 全てのセグ(A-G,DP)をクリア
  clearSegment();
}
 
void loop() {
  Serial.print("Dig="+String(digitsize)+", Seg="+String(segsize)+"\n");
  Serial.print("dig(配列)="+String(alldig[0])+String(alldig[1])+String(alldig[2])+String(alldig[3])+"\n");
  
  // DIG.1(桁数1) → 1を表示
  clearSegment(); clearDigit();  
  digitalWrite(dig1,LOW); 
  digitalWrite(b,HIGH);  digitalWrite(c,HIGH);
  delayMicroseconds(50);
  
  // DIG.2(桁数2) → 2を表示
  clearSegment(); clearDigit(); 
  digitalWrite(dig2,LOW);
  digitalWrite(a,HIGH);  digitalWrite(b,HIGH);  digitalWrite(d,HIGH); 
  digitalWrite(e,HIGH);  digitalWrite(g,HIGH);  
  delayMicroseconds(50);  
 
  // DIG.3(桁数3) → 3を表示
  clearSegment(); clearDigit();  
  digitalWrite(dig3,LOW); 
  digitalWrite(a,HIGH); digitalWrite(b,HIGH);digitalWrite(c,HIGH);
  digitalWrite(d,HIGH); digitalWrite(g,HIGH);
  delayMicroseconds(50);
 
  // DIG.4(桁数4) → 4を表示
  clearSegment(); clearDigit();  
  digitalWrite(dig4,LOW); 
  digitalWrite(b,HIGH); digitalWrite(c,HIGH);digitalWrite(f,HIGH);
  digitalWrite(g,HIGH);
  delayMicroseconds(50);  
}
