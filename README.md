První úloha: zobrazení prostorové scény obsahující geometrického tělesa definované pomocí rovinného gridu trojúhelníků, s mapováním textury na povrchu, s osvětlením a vrženými stíny
Prostudujte si ukázky programování shaderů v gitovém repositáři.
Vytvořte vertex a index buffer pro uložení geometrie tělesa založené na síti trojúhelníků – grid. Vyzkoušejte implementaci gridu pomoci seznamu (list) i pásu trojúhelníků (strip). Model zobrazte ve formě bodů, hran i vyplněných ploch.
Shaderovým programem ve vertex shaderu modifikujte tvar rovinného gridu na prostorové těleso.
Tělesa definujte pomocí parametrických funkcí uvedených například na následujících odkazech.
https://stemkoski.github.io/Three.js/Graphulus-Surface.html
https://christopherchudzicki.github.io/MathBox-Demos/parametric_surfaces_3D.html
https://www.wolframalpha.com/input/?i=3D+spherical+plot+%283+++cos%5B4%CF%95%5D%29%2C+%7B%CE%B8%2C+0%2C+Pi%7D%2C+%7B%CF%95%2C+0%2C+2Pi%7D
https://www.wolframalpha.com/input/?i=3D+plot+%280.5*cos%28sqrt%2820x%5E2+%2B+20y%5E2%29%29%29%2C+%7Bx%2C-1%2C1%7D%2C+%7By%2C-1%2C1%7D
https://www.wolframalpha.com/widgets/view.jsp?id=f708f36bc40c46f8db505d43ca92053b
Implementujte alespoň 6 funkcí, dvě v kartézských, dvě ve sférických a dvě v cylindrických souřadnicích. Vždy jedna může být použita z uvedených stránek a druhou „pěknou“ navrhněte. Výpočet geometrie zobrazovaných těles (souřadnic vrcholů) i přepočet na zvolený souřadnicový systém bude prováděn vertexovým programem! Vstupem do GPU bude vždy rovinný grid.
Alespoň jednu z funkcí modifikujte v čase pomocí uniform proměnné.
Zobrazte alespoň dvě tělesa zároveň, vypočtené a načtené.
Vytvořte vhodné pixelové programy pro renderování hodnot na povrchu těles znázorňující barevně pozici (souřadnici xyz, hloubku), barvu povrchu, mapovanou texturu, normálu a souřadnice do textury, vzdálenost od zdroje světla, vhodné pro debugování.
Normálu povrchu určete parciální derivací funkce nebo diferencí ve vertex shaderu.
Umožněte změnu modelovací transformace tělesa, animací v čase nebo ovládáním uživatelem.
Pozorovatele (pohledovou transformaci) nastavte pomocí kamery a ovládejte myší (rozhlížení) a klávesami WSAD (pohyb vpřed, vzad, vlevo, vpravo).
Umožněte přepínání ortogonální i perspektivní projekce.
Vytvořte pixelový program pro zobrazení texturovaného osvětleného povrchu pomoci Blinn-Phong osvětlovacího modelu, všechny složky. Jednotlivé složky samostatně zapínejte a umožněte změnu vzájemné polohy světla a tělesa.
Implementujte reflektorový zdroj světla a útlum prostředí. Implementujte hladký přechod na okraji reflektorového světla. Pozici zdroje světla znázorněte.
Vyplňte autoevaluační tabulku k sebehodnocení odevzdaného řešení. Tabulku odevzdejte jako součást odevzdání úlohy v adresáři projektu. Slouží především k shrnutí vámi implementované funkcionality projektu a k zjednodušení hodnocení.
Implementujte jeden postprocessingový průchod vizualizačním řetězcem, který upraví výsledek rendrování prostorové scény vypočtený po prvním průchodu. Využijte RenderTarget pro kreslení do rastrového obrazu, který následně zpracujte v druhém průchodu. Zvolte vhodnou rastrovou operaci, např. rozmazání, zaostření, změnu barev a podobně, a implementujte ji jako fragment shaderovou operaci v samostatném průchodu pipelinou.
V rámci řešení první průběžné úlohy i semestrálního projektu bude požadováno, aby si studenti verzovali (pomocí gitlab.com nebo github.com) postup vývoje aplikace. Vytvořte si privátní repozitář, přidejte vašeho cvičícího a pravidelně ukládejte jednotlivé commity. Finální verzi projektu odevzdejte standardně prostřednictvím Olivy. 
Před odevzdáním si znovu přečtěte pravidla odevzdávání a hodnocení projektů uvedené v Průvodci studiem
