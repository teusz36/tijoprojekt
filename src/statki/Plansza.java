package statki;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Random;
import java.util.Scanner;

/**
 * Klasa odpowiedzialna za obsługę planszy.
 */
public class Plansza {
    private final int szerokoscPlanszy = 10;
    private final int wysokoscPlanszy = 10;
    protected char[][] plansza = new char[szerokoscPlanszy][wysokoscPlanszy];
    private final char[] litery = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
    private final char[] cyfry = {'0', '1', '2', '3', '4'};
    Scanner scanner = new Scanner(System.in);
    int iloscStatkówDoRozmieszczenia = 10, iloscCzteromasztowcowDoRozmieszczenia = 1, iloscTrójmasztowcowDoRozmieszczenia = 2, iloscDwumasztowcowDoRozmieszczenia = 3, iloscJednomasztowcowDoRozmieszczenia = 4, iloscMasztow, koordynatX=0, koordynatY=0;
    char kierunek;
    boolean statekMozeBycUmieszczony;

    /**
     * Konstruktor wypełniający planszę pustymi polami.
     */
    Plansza(){
        for(int i=0; i<szerokoscPlanszy; i++){
            for(int j=0; j<wysokoscPlanszy; j++){
                plansza[i][j] = ' ';
            }
        }
    }

    /**
     * Metoda do generacji planszy gracza.
     * @return plansza z ustawieniem statków gracza
     */
    public Plansza generujPlanszeGracza(){
        for(int i=0; i<szerokoscPlanszy; i++){ //wypełnienie planszy pustymi polami
            for(int j=0; j<wysokoscPlanszy; j++){
                plansza[i][j] = ' ';
            }
        }
        do {
            statekMozeBycUmieszczony = true;
            System.out.println("Statki do rozmieszczenia:\nczteromasztowiec " + iloscCzteromasztowcowDoRozmieszczenia + "\ntrójmasztowiec " + iloscTrójmasztowcowDoRozmieszczenia + "\ndwumasztowiec " + iloscDwumasztowcowDoRozmieszczenia + "\njednomasztowiec " + iloscJednomasztowcowDoRozmieszczenia);
            System.out.println("Podaj umieszczenie statku według podanego przykładu: \n[liczba masztów] [koordynat X(liczba)] [koordynat Y(litera)] [kierunek (g, p, d, l)]\n3 4 a d");
            generujStatek(scanner.nextLine());
        } while (iloscStatkówDoRozmieszczenia != 0);
        usunGwiazdki();
        System.out.println("ready");
        this.rysujPlansze();
        return this;
    }

    private void usunGwiazdki() {
        for(int i=0; i<szerokoscPlanszy; i++){
            for(int j=0; j<wysokoscPlanszy; j++){
                if(plansza[i][j] == '*'){
                    plansza[i][j] = ' ';
                }
            }
        }
    }

    /**
     * Metoda sprawdzająca możliwość generacji statku i generująca go, jeśli można.
     * @param parametry pobrane z klawiatury parametry generacji statku.
     */
    private void generujStatek(String parametry){
        boolean zakazGeneracji = false, statekUmieszczono = false;
        String[] parametryTab = parametry.split(" "); // utworzenie tablicy z podanymi parametrami
        try {
            iloscMasztow = Integer.parseInt(parametryTab[0]);
        } catch (Exception e){
            System.out.println("Błędna długość statku");
            return;
        }
        if(!poprawnaDługośćStatku(iloscMasztow)){
            System.out.println("Nie można osadzić statku o podanej długości");
            return;
        }
        try {
            koordynatX = Integer.parseInt(parametryTab[1]);
        } catch (Exception e){
            System.out.println("Błędny koordynat");
            return;
        }
        if(koordynatX < 1 || koordynatX > 10){
            System.out.println("Koordynat spoza zakresu 1 - 10");
            return;
        }
        koordynatX--; //zmniejszenie koordynatu o 1 z powodu indeksowania tablicy od 0
        koordynatY = zmienLitereNaLiczbe(parametryTab[2]);
        if(koordynatY == 0){
            System.out.println("Błędny koordynat");
            return;
        }
        koordynatY--; //zmniejszenie koordynatu o 1 z powodu indeksowania tablicy od 0
        try {
            kierunek = String.valueOf(parametryTab[3]).toLowerCase().charAt(0);
        } catch (Exception e) {
            kierunek = 'g';
        }
        switch (kierunek) {
            case 'g':
                //sprawdzenie, czy można osadzić statek
                if (koordynatY - iloscMasztow >= -1) {
                    for (int i = 0; i < iloscMasztow; i++) {
                        if (plansza[koordynatY - i][koordynatX] != ' ') {
                            zakazGeneracji = true;
                        }
                    }

                    //osadzenie statku
                    if (!zakazGeneracji) {
                        for (int i = 0; i < iloscMasztow; i++) {
                            this.pustePola(koordynatY - i, koordynatX);
                            plansza[koordynatY - i][koordynatX] = cyfry[iloscMasztow];
                        }
                        statekUmieszczono = true;
                    } else {
                        System.out.println("Nie można umieścić statku");
                    }
                } else {
                    System.out.println("Nie można umieścić statku");
                }
                break;
            case 'p':
                //sprawdzenie, czy można osadzić statek
                if (koordynatX + iloscMasztow < 11) {
                    for (int i = 0; i < iloscMasztow; i++) {
                        if (plansza[koordynatY][koordynatX + i] != ' ') {
                            zakazGeneracji = true;
                        }
                    }

                    //osadzenie statku
                    if (!zakazGeneracji) {
                        for (int i = 0; i < iloscMasztow; i++) {
                            this.pustePola(koordynatY, koordynatX + i);
                            plansza[koordynatY][koordynatX + i] = cyfry[iloscMasztow];
                        }
                        statekUmieszczono = true;
                    } else {
                        System.out.println("Nie można umieścić statku");
                    }
                } else {
                    System.out.println("Nie można umieścić statku");
                }
                break;
            case 'd':
                //sprawdzenie, czy można osadzić statek
                if (koordynatY + iloscMasztow < 11) {
                    for (int i = 0; i < iloscMasztow; i++) {
                        if (plansza[koordynatY + i][koordynatX] != ' ') {
                            zakazGeneracji = true;
                        }
                    }

                    //osadzenie statku
                    if (!zakazGeneracji) {
                        for (int i = 0; i < iloscMasztow; i++) {
                            this.pustePola(koordynatY + i, koordynatX);
                            plansza[koordynatY + i][koordynatX] = cyfry[iloscMasztow];
                        }
                        statekUmieszczono = true;
                    } else {
                        System.out.println("Nie można umieścić statku");
                    }
                } else {
                    System.out.println("Nie można umieścić statku");
                }
                break;
            case 'l':
                //sprawdzenie, czy można osadzić statek
                if (koordynatX - iloscMasztow >= -1) {
                    for (int i = 0; i < iloscMasztow; i++) {
                        if (plansza[koordynatY][koordynatX - i] != ' ') {
                            zakazGeneracji = true;
                        }
                    }

                    //osadzenie statku
                    if (!zakazGeneracji) {
                        for (int i = 0; i < iloscMasztow; i++) {
                            this.pustePola(koordynatY, koordynatX - i);
                            plansza[koordynatY][koordynatX - i] = cyfry[iloscMasztow];
                        }
                        statekUmieszczono = true;
                    } else {
                        System.out.println("Nie można umieścić statku");
                    }
                } else {
                    System.out.println("Nie można umieścić statku");
                }
                break;
            default:
                System.out.println("Błędny kierunek");
        }
        if(statekUmieszczono){
            iloscStatkówDoRozmieszczenia--;
            switch (iloscMasztow) {
                case 1 -> iloscJednomasztowcowDoRozmieszczenia--;
                case 2 -> iloscDwumasztowcowDoRozmieszczenia--;
                case 3 -> iloscTrójmasztowcowDoRozmieszczenia--;
                case 4 -> iloscCzteromasztowcowDoRozmieszczenia--;
            }
        }
        rysujPlansze();
    }

    /**
     * Metoda sprawdzająca możliwość wybrania do osadzenia statku o podanej długosci
     * @param podanaDługość długość statku podana przez użytkownika
     * @return true, jeśli można osadzić statek o podanej długości, false, jeśli nie można
     */
    private boolean poprawnaDługośćStatku(int podanaDługość){
        if(podanaDługość < 1 || podanaDługość > 4){
            return false;
        }
        switch (podanaDługość){
            case 1:
                if(iloscJednomasztowcowDoRozmieszczenia == 0){
                    return false;
                }
                break;
            case 2:
                if(iloscDwumasztowcowDoRozmieszczenia == 0){
                    return false;
                }
                break;
            case 3:
                if(iloscTrójmasztowcowDoRozmieszczenia == 0){
                    return false;
                }
                break;
            case 4:
                if(iloscCzteromasztowcowDoRozmieszczenia == 0){
                    return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Metoda otaczająca pole o podanych koordynatach znakami "*", które oznaczają pole puste.
     * @param koordynatY koordynat Y pola.
     * @param koordynatX koordynat X pola.
     */
    protected void pustePola(int koordynatY, int koordynatX){
        for(int i = koordynatY-1; i < koordynatY+2; i++){
            for(int j = koordynatX-1; j < koordynatX+2; j++){
                if(!(i < 0 || j < 0 || i > 9 || j > 9)) {
                    if (plansza[i][j] == ' ') {
                        plansza[i][j] = '*';
                    }
                }
            }
        }
    }

    /**
     * Metoda, która zmienia podaną literę na odpowiadającą jej liczbę.
     * @param litera litera do zmiany.
     * @return liczba odpowiadająca podanej literze.
     */
    public int zmienLitereNaLiczbe(String litera){
        for(int i=0; i<10; i++){
            if(litery[i] == litera.toUpperCase().charAt(0)){
                return ++i;
            }
        }
        return 0;
    }

    public char zmienLiczbeNaLitere(int liczba){
        return litery[liczba];
    }

    /**
     * Metoda rysująca planszę na ekran.
     */
    public void rysujPlansze(){
        System.out.print("  ");
        for(int i=0; i<szerokoscPlanszy; i++){
            System.out.print(i+1+" ");
        }
        System.out.println();
        for(int i=0; i<szerokoscPlanszy; i++){
            for(int j=0; j<wysokoscPlanszy; j++){
                if(j==0){
                    System.out.print(litery[i]+" ");
                }
                System.out.print(plansza[i][j]+" ");
            }
            System.out.println();
        }
    }

    /**
     * Metoda rysująca planszę gracza na ekran.
     */
    public void rysujPlanszeGracza(Plansza planszaTechniczna){
        System.out.print("  ");
        for(int i=0; i<szerokoscPlanszy; i++){
            System.out.print(i+1+" ");
        }
        System.out.println();
        for(int i=0; i<szerokoscPlanszy; i++){
            for(int j=0; j<wysokoscPlanszy; j++){
                if(j==0){
                    System.out.print(litery[i] + " ");
                }
                if(this.plansza[i][j] == 'x') {
                    System.out.print(plansza[i][j] + " ");
                } else if(planszaTechniczna.plansza[i][j] != ' ' && planszaTechniczna.plansza[i][j] != '*') {
                    System.out.print("o ");
                } else {
                    System.out.print(plansza[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Metoda generująca planszę techniczną dla AI korzystająca z przygotowanych plików.
     * @return plansza z ustawieniem statków AI
     */
    public Plansza generujPlanszeAI(){
        Random rand = new Random();
        int numerPlanszy = rand.nextInt(10);
        int iterator1=0, iterator2=0;
        String liniaZPliku;
        String nazwaPliku = "./AIplansze/AI_plansza_"+numerPlanszy+".txt";
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(nazwaPliku)));
            liniaZPliku = in.readLine();
            in.close();
            for(int i=0; i<wysokoscPlanszy*szerokoscPlanszy; i++){
                if(liniaZPliku.charAt(i) == '0'){
                    plansza[iterator1][iterator2] = ' ';
                } else {
                    plansza[iterator1][iterator2] = liniaZPliku.charAt(i);
                }
                iterator1++;
                if(iterator1 == wysokoscPlanszy){
                    iterator2++;
                    iterator1=0;
                }
            }
        } catch (Exception e){
            System.out.println("Brak pliku");
        }
        return this;
    }
}
