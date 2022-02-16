//↓4桁7セグLED
//桁指定用のピン(10秒~0.01秒)
const int dig1 = 17;
const int dig2 = 16;
const int dig3 = 22;
const int dig4 = 2;
//点灯場所指定用のピン(a~dp)
const int a = 23;
const int b = 26;
const int c = 18;
const int d = 21;
const int e = 4;
const int f = 25;
const int g = 5;
const int dp = 19;

//↓追加：配列に入れてまとめて作業できるようにする
const int alldig[] = {dig1, dig2, dig3, dig4};
const int alllight[] = {a, b, c, d, e, f, g, dp};
const int digSize = sizeof(alldig) / sizeof(int);

//フォトトランジスタのピン
const int firstPhotoPin = A13;  //IO 15Pin
const int endPhotoPin = A16;    //IO 14Pin
//フォトトランジスタの状態フラグ（ストップウォッチの開始・停止）
boolean firstFlagP = false;   //最初は時間の計測がされていない(false)になっている
boolean endFlagP = true;      //最初は時間が止まっている(true)になっている

unsigned long SetMillis;        //loop()の始まる時間
unsigned long StartMillis;      //時間の計測開始
unsigned long ContinueMillis;   //継続時間

int p1_before;  //以前の開始側のフォトトランジスタ値
int p2_before;  //以前の終了側のフォトトランジスタ値

//全てのピンを出力にする
void allOutPut() {
  for (int i = 0; i < 4; i++) {
    pinMode(alldig[i], OUTPUT);
  }
  for (int i = 0; i < 8; i++) {
    pinMode(alllight[i], OUTPUT);
  }
}
// 全てのDIG(桁)をクリア
void clearDigit() {
  for (int i = 0; i < 4; i++) {
    digitalWrite(alldig[i], HIGH);
  }
}
// 全てのセグ(A-G,DP)をクリア
void clearSegment() {
  for (int i = 0; i < 8; i++) {
    digitalWrite(alllight[i], LOW);
  }
}

//7セグLEDの対応した数字の部分を点灯する
void No(int num) {
  switch (num) {
    case 0: //数字「0」のとき
      digitalWrite(a, HIGH);  digitalWrite(b, HIGH);  digitalWrite(c, HIGH);  digitalWrite(d, HIGH);  digitalWrite(e, HIGH);  digitalWrite(f, HIGH); \
      break;
    case 1: //数字「1」のとき
      digitalWrite(b, HIGH);  digitalWrite(c, HIGH);
      break;
    case 2: //数字「2」のとき
      digitalWrite(a, HIGH);  digitalWrite(b, HIGH);  digitalWrite(d, HIGH);  digitalWrite(e, HIGH);  digitalWrite(g, HIGH);
      break;
    case 3: //数字「3」のとき
      digitalWrite(a, HIGH);  digitalWrite(b, HIGH);  digitalWrite(c, HIGH);  digitalWrite(d, HIGH);  digitalWrite(g, HIGH);
      break;
    case 4: //数字「4」のとき
      digitalWrite(b, HIGH);  digitalWrite(c, HIGH);  digitalWrite(f, HIGH);  digitalWrite(g, HIGH);
      break;
    case 5: //数字「5」のとき
      digitalWrite(a, HIGH);  digitalWrite(c, HIGH);  digitalWrite(d, HIGH);  digitalWrite(f, HIGH);  digitalWrite(g, HIGH);
      break;
    case 6: //数字「6」のとき
      digitalWrite(a, HIGH);  digitalWrite(c, HIGH);  digitalWrite(d, HIGH);  digitalWrite(e, HIGH);  digitalWrite(f, HIGH);  digitalWrite(g, HIGH);
      break;
    case 7: //数字「7」のとき
      digitalWrite(a, HIGH);  digitalWrite(b, HIGH);  digitalWrite(c, HIGH);
      break;
    case 8: //数字「8」のとき
      digitalWrite(a, HIGH);  digitalWrite(b, HIGH);  digitalWrite(c, HIGH);  digitalWrite(d, HIGH);  digitalWrite(e, HIGH);  digitalWrite(f, HIGH);  digitalWrite(g, HIGH);
      break;
    case 9: //数字「9」のとき
      digitalWrite(a, HIGH);  digitalWrite(b, HIGH);  digitalWrite(c, HIGH);  digitalWrite(d, HIGH);  digitalWrite(f, HIGH);  digitalWrite(g, HIGH);
      break;
  }
}

void setup() {
  // 全てのピンを出力にする
  allOutPut();

  // 全てのDIG(桁)をクリア
  clearDigit();

  // 全てのセグ(A-G,DP)をクリア
  clearSegment();
  Serial.begin(115200);

  //loop()の始まる時間を設定
  SetMillis = millis();

}

void loop() {
  int p1 = analogRead(firstPhotoPin); //スタート(最初)の光量の取得
  int p2 = analogRead(endPhotoPin);   //ゴール(最後)の光量の取得

  //Serial.printf("最初の光量=%d, 最後の光量=%d\n", p1, p2);

  if (p1_before != (int)NULL && p2_before != (int)NULL) { //以前の値が両方NULLでないとき
    int dif1 = abs(p1 - p1_before);   //以前と今回の開始側のフォトトランジスタ値の差分
    int dif2 = abs(p2 - p2_before);   //以前と今回の停止側のフォトトランジスタ値の差分
    if(dif1 > 100 || dif2 > 100){
      //Serial.printf("最初の差分=%d, 最後の差分=%d\n", dif1, dif2);
    }
    
    if (dif1 > 100) { //車体が開始のゲートを通った(以前の値との差が100以上)とき
      firstFlagP = true;      //開始のゲート = 計測開始(true)
      if (endFlagP == true) { //停止のゲートを通った[計測を止めた(開始の時間の計測停止も含む)]とき
        StartMillis = millis() - SetMillis;   //計測開始
        endFlagP = false;     //停止のゲートを通っていない
      }
    }
    if (dif2 > 100) { //車体が停止のゲートを通った(以前の値との差が100以上)とき
      if (firstFlagP == true) { //開始のゲートを通った[計測が行われている]とき
        endFlagP = true;      //停止のゲート = 計測停止(true)
      }
    }
  }else{
    Serial.println("最初のNULLチェックが行われました");
  }

  if (firstFlagP == true) { //開始のゲートを通った[計測が行われている]とき
    if (endFlagP == false) { //停止のゲートを通っていない[計測を止めた(開始の時間の計測停止も含む)]とき
      ContinueMillis = millis() - StartMillis;    //経過時間を格納
    }
  }

  //Serial.printf("タイム：%dミリ秒\n", ContinueMillis);
  // DIG.1(桁数1) → 1を表示
  clearSegment(); clearDigit(); //全ての点灯をOFFにする
  digitalWrite(dig1, LOW);      //1桁目(Dig1)をONにする
  No(ContinueMillis / 10000 % 10); //数字「1」を表示
  delay(10 / 4);                  //2.5ミリ秒待つ

  // DIG.2(桁数2) → 2を表示
  clearSegment(); clearDigit(); //全ての点灯をOFFにする
  digitalWrite(dig2, LOW);      //2桁目(Dig2)をONにする
  No(ContinueMillis / 1000 % 10); //数字「2」を表示
  digitalWrite(dp, HIGH);       //小数点を光らせる
  delay(10 / 4);                //2.5ミリ秒待つ

  // DIG.3(桁数3) → 3を表示
  clearSegment(); clearDigit(); //全ての点灯をOFFにする
  digitalWrite(dig3, LOW);      //3桁目(Dig3)をONにする
  No(ContinueMillis / 100 % 10); //数字「3」を表示
  delay(10 / 4);                  //2.5ミリ秒待つ

  // DIG.4(桁数4) → 4を表示
  clearSegment(); clearDigit(); //全ての点灯をOFFにする
  digitalWrite(dig4, LOW);      //4桁目(Dig4)をONにする
  No(ContinueMillis / 10 % 10); //数字「4」を表示
  delay(10 / 4);                //2.5ミリ秒待つ

  clearSegment(); clearDigit(); //全ての点灯をOFFにする

  p1_before = p1;   //以前の開始フォトトランジスタ値として格納
  p2_before = p2;   //以前の終了フォトトランジスタ値として格納
}
