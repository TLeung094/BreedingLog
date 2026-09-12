BreedingLog
一個為 Paper / Spigot / Folia 設計的 Minecraft 養殖插件，把原版「餵食即繁殖」替換為關係經營 + 全局成長雙線系統。

簡介
BreedingLog 讓養殖變得更有深度：

好感度線（個體）：每隻動物有獨立心情值，透過摸摸提升、攻擊或頻繁收穫降低。好感度決定能否繁殖。

牧場等級線（全局）：玩家透過收穫或擊殺累積經驗，等級決定掉落表解鎖。

性別線（個體）：每隻動物有性別（公／母），繁殖必須一公一母。

領養系統：野外無主動物可被領養，成為你的牧場成員。

繁殖蛋：孕期結束後母體產下蛋，撿起右鍵即可生成幼崽。

CraftEngine 整合：特殊掉落物與蛋使用 CE 自訂物品，支援自訂紋理。

多語言：繁體中文、英文官方支援，簡體中文由社群貢獻。

完整設計請見 docs/DESIGN.md。

功能一覽
模組	功能
動物領養	空手 + Shift + 右鍵領養野生動物，隨機分配性別
好感度	摸摸 +5，攻擊 −10，頻繁收穫 −3，每日衰減最低降至 50
產物獲取	通用收穫、工具收穫（刷子／剪刀）、被動掉落（糞便、牛奶、蘑菇湯）
繁殖	同類異性、成年、好感度 ≥ 60、非孕期；按動物分級孕期
育幼	孕期結束產下 CE 蛋，撿起右鍵生成幼崽，性別隨機
牧場等級	收穫／擊殺累積經驗，解鎖稀有掉落表
擊殺掉落	成年動物掉落更豐厚，騾亦納入牧場等級
實用指令	toggle、transfer、info、reload、gender、setgender、lang、givece
支援動物
主世界可繁殖動物，共約 22 種：

基礎農場動物：牛、哞菇、綿羊、山羊、豬、雞、兔子

可馴服動物：狼、貓、豹貓、馬、驢、羊駝

特殊繁殖機制：熊貓、狐狸、蜜蜂、海龜、美西螈、青蛙、駱駝、犰狳

不包括：地獄生物（熾足獸、豬布獸）、嗅探獸。

環境需求
項目	需求
伺服器	Paper 26.2 / Spigot 26.2 / Folia
Java	21+
前置插件	CraftEngine 26.x（必需）
可選	SQLite（長期數據備份）
快速開始
1. 下載插件
從 Releases 下載最新 BreedingLog-x.x.x.jar。

2. 安裝前置
將 CraftEngine 放入 plugins/ 資料夾。

3. 安裝插件
將 BreedingLog-x.x.x.jar 放入 plugins/ 資料夾，啟動伺服器。

4. 安裝資源包
將 craftengine/breedinglog_items/ 資料夾複製到：

text
plugins/CraftEngine/resources/breedinglog_items/
重載 CraftEngine 或重啟伺服器。

5. 驗證
進入遊戲，對野生動物空手 + Shift + 右鍵，應該看到領養提示。

指令與權限
指令	功能	權限	預設
/bbreeding toggle	切換指向動物的信息顯示	breedinglog.toggle	所有人
/bbreeding transfer	轉讓指向動物給執行者	breedinglog.transfer	所有人
/bbreeding info	查看自己牧場等級與經驗	breedinglog.info	所有人
/bbreeding gender	查看指向動物性別	breedinglog.gender	所有人
/bbreeding lang <zh_TW|en_US|zh_CN>	切換個人語言	breedinglog.lang	所有人
/bbreeding reload	重載配置	breedinglog.admin	OP
/bbreeding setgender <MALE|FEMALE>	管理員強制設定性別	breedinglog.admin	OP
/bbreeding givece	管理員給予 CE 測試物品	breedinglog.admin	OP
配置文件
文件	說明
config.yml	主配置（孕期時長、好感度參數、經驗公式等）
drops.yml	掉落表（按牧場等級分段，支援 CE 物品）
lang/zh_TW.yml	繁體中文語言檔
lang/en_US.yml	英文語言檔
lang/zh_CN.yml	簡體中文語言檔（社群貢獻）
從源碼構建
bash
git clone https://github.com/yourname/BreedingLog.git
cd BreedingLog
./gradlew build
構建產物位於 build/libs/BreedingLog-x.x.x.jar。

開發環境
JDK 21

Gradle 8.x（使用 wrapper 即可）

IntelliJ IDEA 或 Eclipse

貢獻
歡迎提交 Issue 與 Pull Request。

貢獻語言包
複製 src/main/resources/lang/en_US.yml

翻譯為你的語言，存為 lang/xx_XX.yml

提交 PR

社群貢獻語言包經審核後會納入官方發佈。

貢獻代碼
Fork 本倉庫

建立功能分支（git checkout -b feature/amazing-feature）

提交變更（git commit -m 'Add amazing feature'）

推送分支（git push origin feature/amazing-feature）

開啟 Pull Request

專案結構
text
BreedingLog/
├── README.md                    # 本文件
├── docs/
│   └── DESIGN.md                # 完整設計計劃書
├── LICENSE
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/breedinglog/
│   │   │       ├── BreedingLogPlugin.java
│   │   │       ├── scheduler/       # 調度器抽象層（Paper / Folia）
│   │   │       ├── i18n/            # 國際化
│   │   │       ├── craftengine/     # CE 整合
│   │   │       ├── animal/          # 動物註冊表
│   │   │       ├── data/            # PDC 工具
│   │   │       ├── listener/        # 事件監聽
│   │   │       ├── command/         # 指令
│   │   │       └── config/          # 配置載入
│   │   └── resources/
│   │       ├── plugin.yml
│   │       ├── config.yml
│   │       ├── drops.yml
│   │       └── lang/
│   └── test/
├── craftengine/                 # CE 資源包
│   └── breedinglog_items/
└── .github/
    └── workflows/
        └── build.yml            # CI
開發路線圖
階段	內容	狀態
1	基礎設施（SchedulerAdapter、MessageService、CE Hook）	規劃中
2	好感度 + 性別線	規劃中
3	牧場等級線	規劃中
4	繁殖與育幼	規劃中
5	指令與優化	規劃中
6	可選項（CE 食槽、SQLite 備份等）	待評估
授權
本專案採用 MIT License。

致謝
CraftEngine — 自訂物品與方塊框架

Paper — 高效能伺服器核心

Folia — 分區多線程伺服器核心

Adventure — 文本組件 API

連結
完整設計計劃書

Issue Tracker

討論區
