# MyZamgerInsight-RPR-projekat-
Uvid u Zamger analitiku, prosjek, rank u generaciji kao i rezultate generacije i ostalih generacija od 2010-
Elektrotehnički fakultet Sarajevo
Razvoj programskih rješenja




Izvještaj o projektu

MyZamgerInsight

Student: Huso Hamzić
Predmetni nastavnik: Doc.dr Vedran Ljubović









Sadržaj
Rad	3
Ideje	8
Baza	8
OOP	9
GUI	10
JSON	10
Enumi	11
Veze	11
Threadovi	11
Lambde	12
Jasper	12
Dependency's	13
Planovi	13
Bitno	14











Opis aplikacije
Rad
Aplikacija daje uvid u uspjeh studenata na Elektrotehničkom fakultetu te rangira studente po njihovom uspjehu u svojoj generaciji. Omogućava korisniku da vidi svoj uspjeh(ocjene iz predmeta, ukupan prosjek itd...) kao i uspjeh svojih kolega iz generacije te ostalih generacija(od 2010. – do danas). 
Korisnik se prvo suočava sa login screenom koji traži da se u aplikaciju loguje sa Zamger pristupnim podacima(ovo je stavljeno kao neki 'osigurač' koji garantira da ovako pa možemo reći moćan alat ne mogu koristiti osobe van krugova ETF-a). 











Nakon unesenih pristupnih podataka aplikacija šalje request na https://zamger.etf.unsa.ba/wslogin.php odakle preuzima SID pomoću kojeg pristupa informacijama sa Zamgera trenutnog korisnika(ostvarenom uspjehu). Nakon toga slijedi „Loading screen“ iza kojeg se u pozadini vrši formiranje generacije trenutnog korisnika(ranije se zaključilo preko Zamgera koje je godine korisnik upisao 1. godinu na ETF-u) odnosno otvaranje izvještaja one godine kada je student upisao 1. godinu do naredne 3 godine. Ovaj proces traje nekih 4-5 sekundi u zavisnosti specifikacija računara na kom se aplikacija pokrenula. 








Nakon što se prikupljanje podataka završilo korisnika dočekuje „Home screen“ gdje korisnik može viditi osnovni uvid u svoje rezultate i njegove privatne informacije:









Izgled Home screena varira od toga da li je trenutni korisnik muško ili žensko(poruka dobrodošlice je drugačija), da li je korisniku trenutni prosjek veći od 8, da li je završio BSc ili ne itd... Informacije o datumu rođenja, spolu i mjestu rođenja su preuzete pozivom jednog Zamger API-ja i to 





Koji vraća JSON sa extended infos o trenutnom korisniku koji se parsira i očitaju ti podaci.

Klikom na jedno od dugmadi lijevo otvaraju se respektivno prozori „Moji rezultati“










gdje korisnik vidi ocjene iz svih njegovih predmeta(informacije preuzete sa Zamger odjeljka „Pregled ostvarenog rezultata“).

Klikom na dugme „Rezultati moje generacije“ otvara se prozor u kom se prikazuje rang lista generacije trenutnog korisnika sortirane prvo po broju položenih predmeta pa onda po prosjeku. Korisniku se omogućava da vrši pretragu po imenu i prezimenu kao i po broju indexa ili jednostavno klikom na određenog studenta. Odabrani student se „učitava“ u polja lijevo i omogućava se uvid u njegove ocjene na određenim predmetima, njegov rank u generaciji itd...










Analogno pored svoje generacije može se dobiti uvid u bilo koju generaciju od 2010. g do danas(Izvještaji prije 2010. g nisu dostupni osim adminima na Zamgeru) klikom na dugme „Rezultati druge generacije“, koji prvo otvara Dialog box gdje korisnik bira generaciju koju želi vidjeti, odabirom generacije opet se otvara Loading screen gdje se u pozadini u novom threadu formira nova generacija(njihov uspjeh) i koja se rangira







Dugme „Izvezi“ otvara izvještaj(JesperReport) generacije koju student trenutno gleda i omogućava export rang liste u PDF i slične formate.

U Menu baru korisnik može mijenjati jezik interface-a. Podržana su dva jezika Bosanski i Engleski (iako Zamger sam po sebi nije bilingualan) ali je to urađeno da bi se pokrio zahtjev projekta da se mora koristiti sve što smo naučili na predmetu.
Također u Menu baru korisnik može prekinuti rad aplikacije klikom na item „Izadji“ a također može i vidjeti osnovne informacije o projektu klikom na item „O nama“.

Nakon svega omogućena je odjava iz aplikacije pomoću dugmeta „Odjavi se“ koje korisnika vraća ponovo na Login screen.
Ovim je ukratko opisan rad aplikacije.





Osnovne ideje pri implementaciji
Ideje
Prilikom implementacije korištena je SQLite baza podataka koja je veoma fleksibilna i portabilna a i sasvim dovoljan za „razmjere“ ovog projekta. Komunikacija sa bazom se vrši preko klase RezultatiDAO. 
Također korištene su mnoge klase učitane preko Maven dependencies-a koje pomažu pri obavljanju nekih operacija, npr JSoup dependency koji omogućava lagano scrappanje podataka sa određene stranice kao i dependency koji omogućavaju slanje requesta konkretno prema Zamgeru(Za detalje pogledati pom.xml).
Podaci se iz baze učitavaju u objekte tipa Student i Predmet koje prate Java Beans specifikaciju.
Izgled aplikacije je konzistentan kroz čitav tok programa što se može i primjetiti na ranijim slikama i nije trivijalan, autor je pazio na dizajn interfacea kako bi ovo sve bilo hajmo reći fino.
Izvještaj je kreiran pomoću JasperSoft studija


Baza podataka
Baza
Kao što je već rečeno u projektu je korištena SQLite baza podataka. Ona nije pretjerano kompleksna, sastoji se od dvije tabele Student i Ocjena gdje tabela Student sadrži primary key broj indeksa i ime i prezime, dok tabela Ocjena sadrži ime predmeta, ocjenu iz predmeta i foreign key na određenog studenta. 
Kako program barata sa malo većim podacima(npr u mojoj generaciji postoji 561 student) pazilo se kako se podaci upisuju u bazu podataka jer je brzina ovdje krucijalna(besmisleno je čekati 1min da baza odradi svoje), pa je upis rađen preko samo jednog execute statementa što je značajno doprinjelo brzini rada same aplikacije. Također baza posjeduje i tabelu etfIndeksi koja sadrži imena i broj indeksa studenata ETF-a do kojih nije dođeno na ilegalan način, sve informacije su dostupne na internetu uz malo bolji Google search. Gledajući sa aspekta rada aplikacije baza ovdje i nije bila potrebna(osim za čuvanje imena i indeksa svih studenata a i radi izvještaja) jer aplikacija komunicira sa Zamgerom. Bazi se pristupa pomoću singleton klase RezultatiDAO sa najjednostavnijim upitima koje ne treba pretjerano objašnjavati.

Koncepti OOP
OOP
U projektu je enkapsulacija očita, polimorfizam i nasljeđivanje nisu potrebni(nisu izostavljeni zato što autor ne zna da naslijedi neku klasu ili nešto već zato što je tema takva da se nema šta naslijediti kada se barata samo sa studentima). Postoje klase Student i UserInfo koja odvaja korisnika i studenta. UserInfo se mogla naslijediti iz Studenta ali kako se aplikacija misli nadograđivati UserInfo bi se bitno razlikovao od tipa Student pa su klase razdvojene(kako aplikacija samo radi za BSc studij, kada se bude nadograđivala aBd, klasu Student će naslijediti BScStudent i MasterStudent).
Klasa ZamgerApiDemo služi za login na Zamger(morala je biti odvojena jer radi sa cookie-ma autor nije želio klasu UserInfo činiti kompleksnijom već što jest).






Grafički interface
GUI
Prilikom kreiranja aplijacije korišteni su razni gradivni elementi kao što su: ListView, BorderPane, AnchorPane, ChoiceBox, RadioButton itd... Kao i neki elementi koji samom interfaceu dodaju novu dimenziju i „živahnost“ ostvarenu pomoću klase učitane iz Mavena (jfoenix) koja daje nove objekte npr JFXButton itd koji su responsive i nisu statični kao oni osnovni prethodno nabrojani elementi. Nije korišteno povezivanje zato jer je onemogućeno korisniku da mijenja podatke o studentima već se jednostavno samo učitavaju podaci u odgovarajuća polja, odnosno aplikaciju možemo posmatrati samo kao „Read-Only“. Kroz čitav interface dominira plava boja sa nijansama ljubičaste i bijele kako interface ne bi bio „monoton“.

Datoteke
JSON
U aplikaciji se konkretno radi samo ja JSON tipom datoteke(ne zato što autor ne zna čitati i pisati u tekstualnu ili bilo koju drugu datoteku već zato što jednostavno to nije potrebno kada je napravljen izvještaj preko JasperSofta a podaci se dobijaju iz baze), koji se parsira nakon poziva prethodno  spomenutog Zamger API-ja iz kog se očitavaju „Extended infos“ o trenutnom korisniku kao što su datum, mjesto rođenja spol itd...

Enumi i interfejsi
Enumi
U aplikaciji je korišten Enum (MyForm) i interfejsi(MetaData) konkretno iz dependency-a za Google formu jer je u aplikaciju ugrađeno da se prilikom logiranja korisnika ispuni Google forma koja vrati feedback autoru (Ime i prezime osobe koja se logirala s porukom „Koristim tvoju MyZamgerInsight app“) kako bi autor imao uvid u statistiku korištenja aplikacije ta kako bi iste osobe mogle prijaviti neke bug-ove kojih će vjerovatno biti.
Mrežno programiranje i threadovi
Veze
Već smo se dotakli ove teme i ranije u dokumentu, znači aplikacija se 90% oslanja na komunikaciju sa Zamgerom koja je ostvarena preko client-a i pozivom određenih API-ja kao i scrappanjem podataka sa Zamger izvještaja(ovo je loše ali nije moglo drugačije zato što iz nekog nepoznatog razloga pozivi nekih endpoint-a vrate 401 error iako piše u opisu da se mogu pozvati sa student privilegijama). Također aplikacija ispunjava i Google formu. Bitno je napomenuti da se pristupni podaci ne pamte nigdje u programu a pogotovo ne da se šalju van aplikacije što bi narušilo sva moralna prava i etiku programiranja. Login na Zamger se vrši iz klase ZamgerApiDemo koja kreira trenutnog korisnika (tip UserInfo) koji dalje poziva svoje metode za inicijalizaciju generacije, postavljanje svojih extended info's, formiranje generacije itd... 

Threadovi
Što se tiče threadova ovo je delikatna tema pa ćemo se ovdje malo više raspisati. Naime kada je autor prvobitno iskucao program, podaci su prikupljani jednom for-petljom te se na dolazak sa Login screena na Home screen čekalo u prosjeku 1min 47sec što je bilo neprihvatljivo. Problem je bio kako ovo paralelizovat jer je za formiranje generacije(3 godine studija gdje bi se otvorilo ukupno 217 izvještaja) trebalo jako puno vremena, a i nije se moglo samo tako paralelizovat jer je bilo potrebno imati stanja(da je ovaj čitav proces stateless, paralelizovanje bi bilo lahko) pa su se javljali problemi sa konkurentnosti u podacima. Autor se duži vremenski period bavio ovim problemom i uspio je naći način, kao što se može iz koda vidjeti, aplikacija sadrži Task-ove koji su pretežno zaduženi za prikupljanje podataka odnosno formiranje neke generacije sa Zamger izvještaja, odnosno kada god se korisnik suoči sa Loading screenom tada se u pozadini u novom threadu obavlja neki zadatak. Međutim ti taskovi su samo omogućavali da se interface ne zamrzne ali vrijeme potrebno je idalje ostalo 1min 47sec. Tada čitajući dokumentaciju autor je skontao da može iskoristiti jednu novinu u Javi koja se zove parallelStream te je izvršena prepravka koda tako da se više izvještaja odjednom može obrađivati i da se Studenti mogu formirati i ubacivati u kolekciju tipa ConcurrentHashMap. Rezultati su bili fascinantni, sa 1min 47sec vrijeme izvršavanja je spušteno na < 7 sekundi te je aplikacija bila puno upotrebljivija.
Funkcionalno programiranje 
Lambde
U aplikaciji su korištene lambda funkcije(npr kod sortiranja studenata), te kod Task-ova.

Izvještaji
Jasper
Izvještaji se dobijaju kao što je ranije rečeno klikom na dugme Izvezi/Export. Oni su napravljeni pomoću JasperSoft studija. Izvještaj sadrži ranking studenata prvenstveno po broju položenih predmeta pa onda po prosjeku, koji se može izvesti kao .PDF i slično.

Maven
Dependency's
U aplikaciji prednost Jave i Mavena je veoma dobro iskorištena tako što autor nije izmišljao „toplu-vodu“ već je koristio biblioteke koje su ranije razvijene od strane drugih ljudi(npr za Google forms, onda Jfoenix za ljepši izgled GUI-a kao i svi ostali potrebni dependency za rad programa kao sqlite driver i jasperreports). Kreiran je takozvani fat jar koji omogućava da se program pokrene iz komandne linije.

Planovi za budućnost(unapređenje)
Planovi
Autor prvenstveno razmišlja da unaprijedi ovu aplikaciju to jest da je proširi kao prvo na Master studij a i onda da doda još novih feature-sa. U planu je da se ostvari čitanje poruka sa Zamgera(pozivom Zamger API-ja), također da se baza preuredi jer klasa Predmeti je bespotrebna ali od silnih obaveza na 3. semestru autor ni dan danas ne zna kako je izdvojio cca. 50 sati aktivnog rada da uradi ovaj projekat. Također u planu je dodati i dugme „Top lista TP-ista“ koje bi izlistalo rank studenata na TP zadaćama (po kriterijima profesora Jurića) a i da se za svaki predmet pored ocjene uzima i broj bodova ostvaren na predmetu za svakog studenta. To bi omogućilo naprimjer klikom na predmet koji pohađate ili koji ste pohađali da dobijete informaciju top-percent(naprimjer ako imate visoke bodove iz RPR-a da vam aplikacija izbaci „Čestitamo u top 5% ste na ovom predmetu“ ili u suprotnom da vas motivira nekom porukom), kao i još puno puno nadogradnji aBd.


Napomena
Bitno
Autor je svjestan da aplikacija barata sa „osjetljivim“ informacijama i radi toga je prvenstveno stavljen Zamger login jer autor je mišljenja da ovo sve mora ostati u krugovima ETF-a te da nikako ne smije izaći van njih. Još jednom napominjem da informacije o studentima nisu prikupljane ilegalno već da su one fakat dostupne na internetu te da ih autor ni na kakav način ne zloupotrebljava. Kao što je već navedeno aplikacija se može posmatrati kao „Read-Only“ zato se i zove MyZamgerInsight.
