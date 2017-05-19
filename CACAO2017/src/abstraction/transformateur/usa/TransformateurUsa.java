package abstraction.transformateur.usa;
import java.util.ArrayList;

import abstraction.fourni.Acteur;
import abstraction.fourni.Indicateur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;
import abstraction.transformateur.usa.interfacemarche.*;
//Souchu
public class TransformateurUsa implements transformateur,Acteur{
	private StockProduitsFinis finis;
	private StockMatPremiere premiere;
	private TransfoChocolat Transfo;
	private Tresorerie tresorie=new Tresorerie(5000000);
	private static final int uniteventechocolat=10000;//10000 tonnes
	private static final double bornesmax=0.008;//Une unité d'argent =1 million d'euro
	private static final double bornesmin=0.004;
	private static final double stockdesire=200*uniteventechocolat;
	private static final double prixstockage=0.25*bornesmin/(24*1000);//Le prix du stokage par an est de 25% de la valeur des marchandises stockées
	private ArrayList<Double> prixmatprem;
	private double venteChocolat;
	private double achatCacao;
	private Indicateur achats;
	private Indicateur ventes;
	private Indicateur solde;
	private Journal journal;

	/* Nos indicateurs sont :
	 * -Compte courant de la Trésorie
	 * -Dernières vente de Chocolat
	 * -Derniers achat de Cacao
	 */

	public double MiseAJourVente(){
		double tampon = this.venteChocolat;
		this.venteChocolat=0;
		return tampon;
	}

	public double getAchatCacao(){
		return this.achatCacao;
	}


	public TransformateurUsa(){
		journal=new Journal("TransfoUsa");
		prixmatprem = new ArrayList<Double>();
		prixmatprem.add(0.000350);//Prix matière première à la tonne en euros.
		prixmatprem.add(0.000025);
		prixmatprem.add(0.000400);
		finis = new StockProduitsFinis(stockdesire);
		premiere =new StockMatPremiere(stockdesire/2,stockdesire,stockdesire,stockdesire);
		Transfo =new TransfoChocolat(premiere,finis);
		this.venteChocolat=0;
		this.achatCacao=0;
		System.out.println();
		this.achats= new Indicateur("5_TRAN_USA_achats",this,0.0);
		Monde.LE_MONDE.ajouterIndicateur(this.achats);
		this.ventes= new Indicateur("5_TRAN_USA_ventes",this,0.0);
		Monde.LE_MONDE.ajouterIndicateur(this.ventes);
		this.solde=new Indicateur("5_TRAN_USA_solde",this,0.0);
		Monde.LE_MONDE.ajouterIndicateur(this.solde);	
	}

	public void next(){
		this.journal.ajouter("Journal des Transformateur USA");
		produirechocolat();
		payerstock();
		achetermatierepremiere();
		if(this.achats!=null){
			this.achats.setValeur(this, this.getAchatCacao());
			this.ventes.setValeur(this, this.MiseAJourVente());
			this.solde.setValeur(this, this.tresorie.getCompteCourant());
			this.achatCacao=0;
		}
		System.out.println(journal);
	}
	//souchu

	private void payerstock(){
		double avant=this.tresorie.getCompteCourant();
		this.tresorie.removeMoney(this.finis.getStockChocolat()*prixstockage);	
		for (int i=0;i<3;i++){
			this.tresorie.removeMoney(this.premiere.getIngredient(i)*prixstockage);	
		}
		journal.ajouter("Cout stockage = "+(avant-this.tresorie.getCompteCourant()));
	}

	public void achetermatierepremiere(){
		for (int i=1;i<4;i++){
			if (tresorie.getCompteCourant()-((stockdesire-this.premiere.getIngredient(i))*prixmatprem.get(i-1))>0){
				tresorie.setCompteCourant(tresorie.getCompteCourant()-((stockdesire-this.premiere.getIngredient(i))*prixmatprem.get(i-1)));
				this.premiere.setIngredient(i, stockdesire-this.premiere.getIngredient(i));
			}
		}
	}

	private void produirechocolat(){
		double StockSouhaite =200*uniteventechocolat;
		Transfo.produireChoco(StockSouhaite-finis.getStockChocolat());
	}

	public double getprixMin(){
		if (finis.getStockChocolat()<uniteventechocolat){
			journal.ajouter("Prix min="+bornesmax+1);
			return bornesmax+1;
		}
		else if (finis.getStockChocolat()<200*uniteventechocolat){
			double prix= bornesmax-((finis.getStockChocolat()-1*uniteventechocolat)/((200-1)*uniteventechocolat)*(bornesmax-bornesmin));
			journal.ajouter("Prix min="+prix);
			return prix;
		}
		else{
			journal.ajouter("Prix min="+bornesmin);
			return bornesmin;
		}
	}
	@Override
	public void notif(double prix, double quantite) {
		//System.out.println(prix+"    "+quantité);
		this.venteChocolat+=quantite;
		this.finis.enleverChoco(quantite);
		this.tresorie.setCompteCourant(this.tresorie.getCompteCourant()+quantite*prix);	
	}

	public double QteSouhaite(){
		System.out.println("Quantite souhaitée "+(stockdesire-this.premiere.getCacao()));
		if (stockdesire-this.premiere.getCacao()>=0){
			return stockdesire-this.premiere.getCacao();
		}	
		return 0;
	}
	@Override
	public String getNom() {
		return "Transfo Usa";
	}

	public void notificationAchat(double achete, double prix){
		journal.ajouter("On a achete"+achete*prix);
		this.tresorie.setCompteCourant(tresorie.getCompteCourant()-prix*achete);
		this.premiere.setCacao(premiere.getCacao()+achete);
		this.achatCacao+=achete;
	}

	public int hashCode() {
		return this.getNom().hashCode();
	}

	public String toString(){
		return "Nom = "+this.getNom()+"Tresorie ="+this.tresorie.getCompteCourant()+" StockChoco "+this.finis.getStockChocolat();
	}

	public void test(){
		this.notificationAchat(100*uniteventechocolat, 200);
		System.out.println(this.toString());
		this.notif(6000, 100*uniteventechocolat);	
	}
	
	public void testPrixMin(){
		System.out.println("Prixmin= "+this.getprixMin());
		this.notif(10000, uniteventechocolat);
	}

}
