# android-encrypted-messaging
Encrypted chat for Android.

Приложение-мессенджер на языке Kotlin под ОС Android.
При подключении в разработанный протокол заложено установление сеансового ключа связи. Используется криптографическая библиотека Bouncy Castle, предоставляющая одноимённого криптопровайдера.

Графический интерфейс приложения выполнен с использованием библиотек Groupie, Picasso. 

Окно регистрации пользователей выглядят следующим образом (на рисунках продемонстрированы ошибки ввода учетных данных для нового пользователя):

<img width="300px" src="images/Рисунок1.jpg">

<img width="300px" src="images/Рисунок2.jpg">

<img width="300px" src="images/Рисунок3.jpg">

Так же есть возможность входа в существующий аккаунт (на рисунках продемонстрированы ошибки ввода учетных данных.:

<img width="300px" src="images/Рисунок4.jpg">

<img width="300px" src="images/Рисунок5.jpg">

Обмен сообщениями и список диалогов в приложении выглядят следующем образом:

<img width="300px" src="images/Рисунок6.jpg">

<img width="300px" src="images/Рисунок7.jpg">

<img width="300px" src="images/Рисунок8.jpg">

Сообщения хранятся в базе данных Firebase в зашифрованном виде:

<img width="300px" src="images/Рисунок9.png">
