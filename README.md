
# Music Application 

Приложение, позволяющее воспроизводить музыкальные треки с устройства, а также получать их с помощью открытого API [Deezer API](https://developers.deezer.com/api)

## Как запустить приложение

Вы можете [скачать apk-файл](https://drive.google.com/file/d/15i5aSqPPdkzU7ZGgratLg_845yoUZKaJ/view?usp=sharing) или сделать git pull репозитория и собрать. При загрузке необходимо дать разрешение на проигрывание музыки и уведомления.

## Из чего состоит приложение


### 1. Экран локальных треков с телефона

При отркытии приложения пользователь попадает на страницу треков с устройства.

![local screen](https://github.com/MargaritaSkokova/MusicApp/raw/main/app/src/main/res/drawable/local.jpg)

Можно нажать на поиск и ввести запрос, тогда отобразятся найденные песни с устройства

![seacrh on local screen](https://github.com/MargaritaSkokova/MusicApp/raw/main/app/src/main/res/drawable/local_search.jpg)
### 2. Экран треков с API

С помощью панели навигации можно попасть на экран с треками, загруженными по ссылке https://api.deezer.com/chart

![online screen](https://github.com/MargaritaSkokova/MusicApp/raw/main/app/src/main/res/drawable/online.jpg)

Можно нажать на поиск и ввести запрос, тогда отобразятся найденные онлайн песни

![seacrh on online screen](https://github.com/MargaritaSkokova/MusicApp/raw/main/app/src/main/res/drawable/online_search.jpg)

### 3. Экран воспроизведения треков 

При нажатии на песню на любом из двух экранов открывается экран проигрывания песни, откуда можно выйти при нажатии на стрелку в верхнем левом углу экрана.
Можно:
- приостанавливать/продолжать проигрывание
- перематывать ползунком
- переключать на следующую/предыдущую песню

![player](https://github.com/MargaritaSkokova/MusicApp/raw/main/app/src/main/res/drawable/player.jpg)

При возвращении на экран со списком песен, внизу будет превью проигрываемой песни, на котором можно остановить/продолжить проигрывание песни. Также можно переключаться между экранами онлайн/локальной музыки, воспроизведение продолжится.

![player small](https://github.com/MargaritaSkokova/MusicApp/raw/main/app/src/main/res/drawable/player_small.jpg)

### 4. Уведомление с фоновым проигрванием

В панели уведомлений будет отображаться проигрывание песни с возможностью остановить/продолжить его.

![notification](https://github.com/MargaritaSkokova/MusicApp/raw/main/app/src/main/res/drawable/notification.jpg)


## Проблемы и вопросы

Воспроизведение треков из Api делала не отдельным запросом, а проигрыванием по ссылке, полученной при первоначальной загрузке. Тем не менее такой метод существует в сервисе.
