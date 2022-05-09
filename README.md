# 制作のまとめGitHub(IoriFujimura)

[![hackmd-github-sync-badge](https://hackmd.io/S2YPlCf6Skm6G75rnTZXmA/badge)](https://hackmd.io/S2YPlCf6Skm6G75rnTZXmA)

* 自分の実績をまとめているGithubです。

<details>
<summary>

### フォルダの命名規則
</summary>

* 分類
  - Peer: ピア活動<br>※ピア活動とは後輩の授業にチューターの学生が参加して先生の手が足りない状況をサポートすること
  - Team: チーム制作
  - Zemi: 組込系ゼミ
* 学年
  - #3: ３年次
  - #4: ４年次
  - SIC: SIC<br>※SICとはECCで夏休み2週間かけて行うチーム制作のこと
* 時期
  - B4: 前期（4~9月頃）
  - AFT: 後期（10~3月頃）
* 名前
  - _<作品名>: アンダーバーの後に作品名
</details>


<details>
<summary>
 
### EmergencyContact(ピア活動・4年・前期)※ここをクリックすると展開
</summary>
    
* ２年次の学生の開発内容の決まっているチーム制作の「緊急連絡掲示板」の作成です

---

### **指定、決まり**
* 作成物が決まっている
<br>※制作期間は第1フェーズ[4/11～5/19（5週目まで）]のみ、第2フェーズ[5/20～6/30（11週目まで）]は企画→制作が自由
* BacklogやGitなどのタスク管理を行えるようになる
* 2年生は｢ApacheとMySQL｣がインストールされており、ローカルでの実行で使用する
* 最終的に｢用意されているサーバー｣で実装する
* 開発言語は｢PHP｣を想定

### **開発内容**
  - ## **｢緊急時の連絡用掲示板｣**

### **開発理由**
* 社内の緊急用の安否確認のシステムがないことを解決するため

### **補足**
* 設計書、要件定義書、画面遷移図は書かなくても良い
* ログイン、新規登録の機能がある（社員のみが使うが登録画面も作る）
* Excel、テーブルのような全員の一覧（社員名、役職、安否情報など）が見れる画面
* 掲示板となっているが、投稿機能は不要（時間があれば作っても良い）
<br>※ただし、自身の被害状況を報告する投稿機能は必要
* 完成品、作成例はない

### **ピアチューターのやること**
* 学生の困っていることを解決する
* 緊急時の安否確認サイトの作成の手助け
* 完成品がないので今回作成しました。

---

## **確認画像**
![シス開1_ログイン](https://user-images.githubusercontent.com/93113173/167468888-30a32d2c-a10b-4d48-86c8-a4d8061ab1a5.png)
![シス開1_登録](https://user-images.githubusercontent.com/93113173/167468945-f3b46e3f-dd85-4ece-9933-e731c8d56232.png)
![シス開1_登録確認](https://user-images.githubusercontent.com/93113173/167468961-e8f129eb-8f54-45e9-a91d-56417720f209.png)
![シス開1_入力](https://user-images.githubusercontent.com/93113173/167468996-907177c6-2c7d-4527-a1dd-f0f21e63a321.png)
![シス開1_掲示板](https://user-images.githubusercontent.com/93113173/167469008-b17daa63-4fbc-4dca-bdbb-79c4437feeed.png)
</details>


<details>
<summary>
 
### QRCode_RT(チーム制作・3年・後期)※ここをクリックすると展開
</summary>

* QRコードを読み取って翻訳するアプリです。
* 文字の読み取りは画像認識(OCR)で行うことが可能になりました。ただ、OCRには文章が斜めや歪んでいるとき、文字が欠損しているときに読み取ることができないという問題がありました。
* そこで、問題に対応できるQRコードに情報をデータとして受け渡すことで解決しようと試みた制作です。<br>

使用例）：
  * 看板や広告にQRコードを添えることで外国の方でも「QRCode_RT」を使用することで書かれた内容を理解できる
  * メニュー表に添えることでお客様が「QRCode_RT」を使用することで、どのようなモノか把握することができる

https://user-images.githubusercontent.com/93113173/163839920-329fb6c4-8458-4163-9eab-7b5bd4e8115f.mp4
</details>


<details>
<summary>
 
### Wisper(チーム制作・3年・前期)※ここをクリックすると展開
</summary>

* チーム制作で開発内容が決まっていたモノです。
* 簡単なSNSアプリです。

## **確認画像**
<img src="https://user-images.githubusercontent.com/93113173/167469428-88031696-31fd-4414-898a-7ec4db0ff5b6.jpg" width="25%" alt="ログイン画面">
<img src="https://user-images.githubusercontent.com/93113173/167469442-86352127-dc98-439e-81d8-ce72db048c20.jpg" width="25%" alt="新規登録画面">
<img src="https://user-images.githubusercontent.com/93113173/167469469-468211a2-2e9a-4509-9516-8321da7a9951.jpg" width="25%" alt="ログイン後の最初の画面">
<img src="https://user-images.githubusercontent.com/93113173/167469510-ea31453e-5825-46a1-a2a1-d3a64cf32843.jpg" width="25%" alt="検索（ユーザー）画面">
<img src="https://user-images.githubusercontent.com/93113173/167469517-d9262231-759d-4bed-8187-7ea2bfee71f1.jpg" width="25%" alt="検索（投稿）画面">
<img src="https://user-images.githubusercontent.com/93113173/167469549-30180a4c-8b81-48e0-94cf-abab2c229901.jpg" width="25%" alt="投稿画面">
<img src="https://user-images.githubusercontent.com/93113173/167469680-8852139b-0dd9-4735-b355-13f2a5fa5efa.jpg" width="25%" alt="プロフィール編集（最初）画面">
<img src="https://user-images.githubusercontent.com/93113173/167469668-f497b82b-84e1-440e-a266-48808300fd4c.jpg" width="25%" alt="プロフィール編集（選択リスト）画面">
<img src="https://user-images.githubusercontent.com/93113173/167469597-e71e3ad6-0d1e-4500-96c0-3078ad3f5a7f.jpg" width="25%" alt="タイムライン（自分の投稿）画面">
<img src="https://user-images.githubusercontent.com/93113173/167469604-24c2295e-2d67-49aa-85b4-9f5505fb1376.jpg" width="25%" alt="タイムライン（いいね）画面">
<img src="https://user-images.githubusercontent.com/93113173/167469694-97ca1b20-2ec9-45d3-bb82-0f1a2e68b4ee.jpg" width="25%" alt="フォロー確認画面">
<img src="https://user-images.githubusercontent.com/93113173/167469704-bb377339-f417-4854-ba09-b0d7080f96df.jpg" width="25%" alt="フォロワー確認画面">
</details>


<details>
<summary>
 
### ThreeLines(チーム制作・3年・SIC)※ここをクリックすると展開
</summary>

* 夏休み2週間程の期間で行うチーム制作で、「コミュニケーション」をテーマに「４人３目並べ」を制作しました。
* 有名な「〇×ゲーム」を４人で行うイメージです。
    
* <h3>ルール</h3>

  - 勝利条件
    * 合計１０点で勝ち
    * または縦横斜めを1つずつ揃えると勝ち
    
  - 得点
    * 揃ったことのない列のリーチを阻止すると1点
    * 揃っていない列を揃えると3点が追加

## **確認画像**
![ThreeLines_Playing](https://user-images.githubusercontent.com/93113173/167469882-54fe2a74-d4c9-4996-8e79-316d7c93af51.png)
</details>


<details>
<summary>
 
### AutoDoor(組み込みゼミ・3年・前期)※ここをクリックすると展開
</summary>

* ゼミの時間に作成した自動ドアの模型です。
* コロナ禍で接触が懸念されます。そこで非接触の仕組みをドアに取り付けることができたら、需要があると考えて制作しました。

 <img src="https://user-images.githubusercontent.com/93113173/164448326-59eb5854-b39d-4f03-9227-58390237daeb.jpg" width="35%">
</details>


<details>
<summary>
 
### RobotTraceStopwatch(組み込みゼミ・3年・後期)※ここをクリックすると展開
</summary>

* ゼミの時間に作成したロボトレースの計測を行うストップウォッチです。

https://user-images.githubusercontent.com/93113173/163949635-5f38c89b-0748-44c6-abf3-5626e1b6e049.mp4
</details>


<details>
<summary>
 
### Fusion360-modeling(組み込みゼミ・4年・前期)※ここをクリックすると展開
</summary>

* Fusion360の機能を用いた3Dモデルの作成を行っています。
* 教科書[^1]で基本的なコマンドや機能について学び、基礎的な知識を身に着けています。
* 学んだことを活かしてロボトレースの本体の修正を行っています。
<br>※本体は前任者がFreeCadでモデリングを行っていたので、Fusion360に合った形に変更しています。

<img src="https://user-images.githubusercontent.com/93113173/164351714-6b62d7c4-60fe-43bc-b9cc-2c84f67e487e.png" width="40%">
<img src="https://user-images.githubusercontent.com/93113173/164351725-d3e2818b-257a-49f9-b981-6e8f7fe5d66b.png" width="50%">
</details>


## 脚注
[^1]:「Fusion 360 マスターズガイド ベーシック編 ～小原照記・藤村祐爾著～」
