# Ewidencja utrzymania ruchu

Aplikacja konsolowa w Javie 21 do analizy maszyn i zleceń
serwisowych na podstawie danych z SampleData.java.

## Funkcje

- Liczenie niezakończonych zleceń według priorytetu.
- Wskazywanie trzech maszyn z największą liczbą awarii.
- Obliczanie średniego czasu naprawy (MTTR) dla hal.
- Walidacja danych maszyn i zleceń.

## Wymagania

- JDK 21.
- Przy pierwszym uruchomieniu dostęp do internetu w celu pobrania
  Gradle i zależności.

Projekt zawiera Gradle Wrapper, więc osobna instalacja Gradle
nie jest wymagana.

## Budowanie i uruchomienie

Polecenia należy wykonywać w głównym katalogu projektu.

```bash
./gradlew build && java -jar build/libs/cmms.jar
```

## Uruchomienie testów

```bash
./gradlew test
```

## Dane

SampleData.java zawiera 9 maszyn i 45 zleceń serwisowych.