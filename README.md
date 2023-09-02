## Проект NoteBookMap

Приложение предназначено для создания заметок (текстовых, фото, аудио, видео) с привязкой к геоданным и возможностью поделиться ими.

Приложение написано на языке `Kotlin`, а весь UI написан с помощью `Jetpack Compose`. 
Используется `Yandex MapKit SDK` для отоброжения карты и заметок на ней в виде маркеров. 
Все данные хранятся в DataStore и базе данных, работа с которой производится через `Room` с помощью корутин. 
Для инъекции зависимостей используется библиотека `Koin`. 
Для загрузки изображений используется библиотека `Coil`. 
При проектировании приложения придерживались принципы `Clean Architectue` и паттерн `MVVM`. 

## Скриншоты

### Карта
<p float="left">
  <img src="/Readme images/MapPreviw1.jpg" width="300" />
  <img src="/Readme images/MapPreviw2.jpg" width="300" />
</p>

### Описание заметки
<p float="left">
  <img src="/Readme images/DescriptionPreview1.jpg" width="300" />
  <img src="/Readme images/DescriptionPreview2.jpg" width="300" />
</p>

### Список
<p float="left">
  <img src="/Readme images/ListPreview1.jpg" width="300" />
</p>

