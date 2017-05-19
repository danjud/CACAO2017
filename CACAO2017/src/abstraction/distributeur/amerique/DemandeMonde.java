package abstraction.distributeur.amerique;

import abstraction.fourni.Monde;

public class DemandeMonde {
	public static double augStVal; //3%
	public static double augPaques;// 4% des ventes de chocolat au total
	public static double augHalloween; //10%
	public static double augNoel; // 8% des ventes de chocolat au total
	public static double commandesAnneeTotales=6000;
	public static double commandesStepFixe=(commandesAnneeTotales-commandesAnneeTotales*(augStVal+augPaques+augHalloween+augNoel))/26;
	public static double pourcentageFixe=0.40;
	public static double pourcentageFluctuantUS;
	public static double pourcentageFluctuantEU;
	
	
	private double demandeUS;
	private double demandeEU;
	
	public DemandeMonde(){
		
	}
	
	public double getDemandeUS(){
		this.calculDemandes();
		return this.demandeUS;
	}
	public double getDemandeEU(){
		this.calculDemandes();
		return this.demandeEU;
	}
	
	public void setDemandeUS(double US){
		this.demandeUS=US;
	}
	public void setDemandeEU(double EU){
		this.demandeEU=EU;
	}
	public double getFluctuantUS(){
		return pourcentageFluctuantUS;
	}
	public double getFluctuantEU(){
		return pourcentageFluctuantEU;
	}
	
	public void calculDemandes(){
		double augSaisonniere=0;
		int date=Monde.LE_MONDE.getStep()%26;
		double coefaleatoire=0.9+Math.random()*0.2;
		if (date==2 || date==3){
			augSaisonniere=augStVal;
		}
		if (date==7 || date==8){
			augSaisonniere=augPaques;
		}
		if (date==20 || date==21){
			augSaisonniere=augHalloween;
		}
		if (date==22 || date==23){
			augSaisonniere=augNoel;
		}
		
		double commandesTotalesStep=(commandesStepFixe+augSaisonniere*commandesAnneeTotales)*coefaleatoire;
		this.setDemandeUS(commandesTotalesStep*(pourcentageFixe+pourcentageFluctuantUS));
		this.setDemandeEU(commandesTotalesStep*(pourcentageFixe+pourcentageFluctuantEU));
	}
	
}
