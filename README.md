# StravaDownloader
UI robot, that downloads all activities from STRAVA to local directory
To build run "mvn package" from project directory (required maven, jdk, ...) 

# Built project (для тех, кто не хочет устанавливать среду разработки, а хочет просто скачать и запустить)
ПРОВЕРЕНО ТОЛЬКО НА WINDOWS, требуется установленный браузер Chrome
Сборка лежит здесь:  
https://yadi.sk/d/lnGpXPCxL0EMrQ  
Качаем всю папку. Если требуется, то устанавливаем jre (Java Runtime Environment). Дальше говорим в командной строке из папки, куда скачали: java -jar StravaDownloader-1.0-jar-with-dependencies.jar  
В целом работает следующим образом: запускаем, открывается браузер, в браузере есть 1 минута, чтобы залогиниться и прокрутить страницу вниз, чтобы была видна кнопка переключения между страницами (2 стрелки вправо-влево внизу списка из 20 тренировок) в списке тренировок. Робот пробежит по всем страницам и получит ссылки на все тренировки. Сложит их в файл links.txt и зависнет :) (это глюк фреймворка, на основе кторого написан робот, разбираться неохота). Останавливаем, закрываем открытый роботом браузер (если требуется). Запускаем снова и снова логинимся (прокручивать страницу не надо). Робот увидит, что есть файл links.txt, загрузит оттуда все линки и будет поочередно скачивать тренировки. Скачанные тренировки записывает в директорию SavedTrainings с именем ID.EXT (id - номер тренировки в страва, ext - расширение, с которым скачался файл). Те ссылки, которые скачал записывает в файл downloaded_links.txt. Если вдруг надо перезапустить, то тренировки из файла downloaded_links.txt робот будет игнорировать и не закачивать их повторно.   
Есть тренировки введенные вручную. Для них нельзя выгрузить тренировку. В этом случае робот считывает описание тренировки и складывает его в ID.txt. Информацию о том, что скачал тренировку записывает в файл downloaded_description_links.txt - если не надо заново пытаться закачать тренировку, то надо содержимое этого файла скопировать в downloaded_links.txt  
Все действия пишутся в log.txt  
Все тренировки загружаются в папку SavedTrainings  
  
Ничего не оптимизировал. У меня на 1151 тренировке список ссылок для скачивания загружался минут 5, все тренировки грузились часа 3 (не засекал).  
  
  
UPD. Добавил скачивание помимо исходника тренировки еще и GPX, т.к. часть тренировок скачивается в формате JSON, данные из которого понимает только Strava
