package statki;

import java.util.Random;
import java.util.Scanner;

/**
 * Klasa, która zarządza i przeprowadza rozgrywkę, przechowuje plansze oraz komunikuje się z użytkownikiem.
 */
public class Rozgrywka {
    private boolean statusRozgrywki = true;

    Scanner scanner = new Scanner(System.in);
    int koordynatX, koordynatY, ilośćNietrafionychMasztówAI = 20, ilośćNietrafionychMasztówGracza = 20, kierunekAI;
    Plansza planszaGracza= new Plansza(); //plansza przechowująca informacje o strzałach AI
    Plansza planszaAI = new Plansza(); //plansza przechowująca informacje o strzałach gracza
    Plansza planszaGraczaTechniczna = new Plansza().generujPlanszeGracza(); //plansza przechowująca informacje o ustawieniu statków Gracza
    Plansza planszaAITechniczna = new Plansza().generujPlanszeAI(); //plansza przechowująca informacje o ustawieniu statków AI
    Random rand = new Random();

    /**
     * Metoda odpowiedzialna za rozgrywkę.
     */
        Rozgrywka() {
            planszaAITechniczna.rysujPlansze();
            while(statusRozgrywki){
                System.out.println("Plansza gracza");
                planszaGracza.rysujPlanszeGracza(planszaGraczaTechniczna);
                System.out.println("Plansza AI");
                planszaAI.rysujPlansze();
                strzałGracza();
                if(statusRozgrywki) {
                    strzałAI();
                }
            }
        }

    /**
     * Metoda wykonująca strzał AI do planszy gracza
     */
    private void strzałAI(){
        boolean trwanieRuchuAI = true;
        while (trwanieRuchuAI){
            int[] koordynaty = sprawdzeniePól(planszaGracza); //uzyskanie koordynatów z funkcji sprawdzeniePól
            koordynatX = koordynaty[0];
            koordynatY = koordynaty[1]; // przypisanie koordynatów do zmiennych
            if(planszaGracza.plansza[koordynatY][koordynatX] != '*' && planszaGracza.plansza[koordynatY][koordynatX] != 'x' ) { //warunek sprawdzający czy pole o podanych koordynatach nie zostało już wcześniej wybrane
                System.out.println("AI strzela w: " + (koordynatX + 1) + ", " + planszaGracza.zmienLiczbeNaLitere(koordynatY));
                if (planszaGraczaTechniczna.plansza[koordynatY][koordynatX] == ' ') { //sprawdzenie, czy w trafionym miejscu nie ma statku
                    System.out.println("Pudło!");
                    planszaGracza.plansza[koordynatY][koordynatX] = '*';
                    trwanieRuchuAI = false; //zakończenie pętli wykonującej ruchy AI
                } else if (planszaGracza.plansza[koordynatY][koordynatX] != '*' && planszaGracza.plansza[koordynatY][koordynatX] != 'x') {
                    System.out.println("Trafiony!");
                    planszaGracza.plansza[koordynatY][koordynatX] = 'x';
                    ilośćNietrafionychMasztówGracza--;
                    statekTrafiony(planszaGracza, planszaGraczaTechniczna, koordynatX, koordynatY);
                    if (ilośćNietrafionychMasztówGracza == 0) {
                        koniecGry("Komputer");
                        statusRozgrywki = false;
                        trwanieRuchuAI = false;
                    }
                }
            }
        }
    }

    private void strzałGracza() {
        System.out.println("Podaj koordynaty([liczba] [litera])");
        String[] koordynaty = scanner.nextLine().split(" ");
        if(koordynaty.length == 2) {
            try {
                koordynatX = Integer.parseInt(koordynaty[0]);
            } catch (Exception e) {
                System.out.println("Niepoprawny koordynat");
                strzałGracza();
                return;
            }
            if (koordynatX < 1 || koordynatX > 10) {
                System.out.println("Koordynat spoza zakresu");
                strzałGracza();
                return;
            }
            koordynatX--; //zmniejszenie koordynatu o 1 z powodu indeksowania tablicy od 0
            koordynatY = planszaGracza.zmienLitereNaLiczbe(koordynaty[1]);
            if (koordynatY == 0) {
                System.out.println("Błędny koordynat");
                strzałGracza();
                return;
            }
            koordynatY--; //zmniejszenie koordynatu o 1 z powodu indeksowania tablicy od 0
            if (planszaAI.plansza[koordynatY][koordynatX] == 'x' || planszaAI.plansza[koordynatY][koordynatX] == '*') {
                System.out.println("Już tu strzelałeś!");
                strzałGracza();
            } else if (planszaAITechniczna.plansza[koordynatY][koordynatX] == ' ') {
                System.out.println("Pudło!");
                planszaAI.plansza[koordynatY][koordynatX] = '*';
            } else if (planszaAI.plansza[koordynatY][koordynatX] != '*' && planszaAI.plansza[koordynatY][koordynatX] != 'x') {
                planszaAI.plansza[koordynatY][koordynatX] = 'x';
                ilośćNietrafionychMasztówAI--;
                statekTrafiony(planszaAI, planszaAITechniczna, koordynatX, koordynatY);
                planszaAI.rysujPlansze();
                if (ilośćNietrafionychMasztówAI == 0) {
                    koniecGry("Gracz");
                    statusRozgrywki = false;
                } else {
                    strzałGracza();
                }
            }
        } else {
            System.out.println("Podaj dwa koordynaty!");
            strzałGracza();
            return;
        }
    }

    /**
     * Sprawdzenie, czy na planszy znajduje się trafiony, ale nie zatopiony statek.
     * @param planszaDoSprawdzenia plansza, która będzie sprawdzana
     * @return tablica int[] przechowująca koordynaty do strzału.
     */
    private int[] sprawdzeniePól(Plansza planszaDoSprawdzenia){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){ //pętle przechodzące przez tablicę
                try {
                    if (planszaDoSprawdzenia.plansza[i][j] == 'x' && (planszaDoSprawdzenia.plansza[i + 1][j] == ' ' || planszaDoSprawdzenia.plansza[i - 1][j] == ' ' || planszaDoSprawdzenia.plansza[i][j + 1] == ' ' || planszaDoSprawdzenia.plansza[i][j - 1] == ' ')) {
                        return wyznaczKierunekAI(i, j);
                    }
                } catch (IndexOutOfBoundsException e) {

                }
            }
        }
        koordynatX = rand.nextInt(10);
        koordynatY = rand.nextInt(10);
        return new int[]{koordynatX, koordynatY};
    }

    /**
     * Metoda wykonywana, gdy na planszy znajduje się trafiony, ale nie zatopiony statek.
     * @param i koordynat niezatopionego statku.
     * @param j koordynat niezatopionego statku.
     * @return tablica int[] przechowująca koordynaty do strzału.
     */
    private int[] wyznaczKierunekAI(int i, int j){
        int[] koordynaty;
        kierunekAI = rand.nextInt(4);
        switch(kierunekAI){
            case 0: //góra
                koordynaty = new int[]{j, i-1};
                if(i-1 >= 0){
                    return koordynaty;
                } else {
                    return wyznaczKierunekAI(i, j);
                }
            case 1: //prawo
                koordynaty = new int[]{j+1, i};
                if(j+1 < 10){
                    return koordynaty;
                } else {
                    return wyznaczKierunekAI(i, j);
                }
            case 2: //dół
                koordynaty = new int[]{j, i+1};
                if(i+1 < 10){
                    return koordynaty;
                } else {
                    return wyznaczKierunekAI(i, j);
                }
            case 3: //lewo
                koordynaty = new int[]{j-1, i};
                if(j-1 >= 0){
                    return koordynaty;
                } else {
                    return wyznaczKierunekAI(i, j);
                }
        }
        return new int[]{0, 0};
    }

    /**
     * Metoda wywoływana po trafieniu statku. Sprawdza, czy statek jest zatopiony. Aktualizuje plansze gry i planszę techniczną.
     * @param planszaGry widzialna dla użytkownika plansza gry.
     * @param planszaTechniczna plansza niewidziana przez użytkownika zawierająca informacje o rozmieszczeniu statków.
     * @param koordynatX koordynat poziomy z planszy.
     * @param koordynatY koordynat pionowy z planszy.
     */
    private void statekTrafiony(Plansza planszaGry, Plansza planszaTechniczna, int koordynatX, int koordynatY){
        if(planszaTechniczna.plansza[koordynatY][koordynatX] == '1'){ //jednomasztowiec, czyli statek trafiony i zatopiony
            planszaTechniczna.plansza[koordynatY][koordynatX] = '0';
            planszaGry.pustePola(koordynatY, koordynatX);
        }
        switch(planszaTechniczna.plansza[koordynatY][koordynatX]){ //aktualizacja wartości trafionego pola na planszy technicznej.
            case '1':
                planszaTechniczna.plansza[koordynatY][koordynatX] = '0';
                break;
            case '2':
                planszaTechniczna.plansza[koordynatY][koordynatX] = '1';
                break;
            case '3':
                planszaTechniczna.plansza[koordynatY][koordynatX] = '2';
                break;
            case '4':
                planszaTechniczna.plansza[koordynatY][koordynatX] = '3';
                break;
        }
        char nowaWartość = planszaTechniczna.plansza[koordynatY][koordynatX]; //nowa wartość przypisana do pola na planszy technicznej.
        for(int i = koordynatY-1; i < koordynatY+2; i++){
            for(int j = koordynatX-1; j < koordynatX+2; j++){ //pętle przechodzące wokół pola.
                if(!(i < 0 || j < 0 || i > 9 || j > 9)) { //sprawdzenie, czy pętle nie wychodzą poza zakres.
                    if (planszaTechniczna.plansza[i][j] != ' ' && planszaTechniczna.plansza[i][j] != nowaWartość) {
                        statekTrafiony(planszaGry, planszaTechniczna, j, i); //rekurencyjne wywołanie metody dla pól, które muszą zostać zaktualizowane na planszy technicznej.
                    }
                }
            }
        }
    }

    /**
     * Metoda wywoływana na koniec gry, kiedy wszystkie ze statków na czyjejś planszy zostały zatopione.
     * @param zwyciezca zwycięzca rozgrywki.
     */
    private void koniecGry(String zwyciezca){
        if(zwyciezca.equals("Gracz")) {
            System.out.println("Koniec gry!\nWygrywasz! Gratulacje ");
        } else {
            System.out.println("Koniec gry!\nWygrywa komputer :c");

        }
    }
}
