import { Component, OnInit } from '@angular/core';
import { ConventionServiceService,AppConvention } from '../../../Services/convention-service.service';
import { AppConventionType, ConventionTypeService } from '../../../Services/convention-type.service';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DocumentsService } from '../../../Services/documents.service';
import { HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-add-convention',
  standalone: false,
  templateUrl: './add-convention.component.html',
  styleUrl: './add-convention.component.css'
})
export class AddConventionComponent implements OnInit{
 conventionForm!: FormGroup;
  selectedTypeCode: string | null = null;
  conventionTypes: AppConventionType[] = [];
successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private conventionService: ConventionServiceService 
  ) {}

  ngOnInit() {
    this.conventionForm = this.fb.group({
      typeCode: [''],
      title: [''],
      conventionNumber: [''],
      object: [''],
      signatureDate: [''],
      startDate: [''],
      endDate: [''],
      partners: [''],
      filePath: [''],

      // Champs spécifiques
      natureEchange: [''],
      modaliteEchange: [''],
      logistique: [''],
      assuranceResponsabilite: [''],
      renouvellement: [''],
      resiliation: [''],

      perimetre: [''],
      modalite: [''],

      customFields: this.fb.array([])
    });

    // Charger les types depuis l’API
    this.conventionService.getConventionTypes().subscribe(types => {
      this.conventionTypes = types;
    });
    

    // Suivi du type sélectionné
    this.conventionForm.get('typeCode')?.valueChanges.subscribe(code => {
      this.selectedTypeCode = code;
    });
  }

  get customFields(): FormArray {
    return this.conventionForm.get('customFields') as FormArray;
  }

  addCustomField(): void {
    this.customFields.push(this.fb.group({ key: [''], value: [''] }));
  }

  removeCustomField(index: number): void {
    this.customFields.removeAt(index);
  }

  submitConvention(): void {
    const raw = this.conventionForm.value;

    // Convertir le tableau de champs personnalisés en map
    const customMap: { [key: string]: string } = {};
    raw.customFields.forEach((f: any) => {
      if (f.key) customMap[f.key] = f.value;
    });

    const selectedType = this.conventionTypes.find(t => t.code === raw.typeCode);
  
    const dto = {
      ...raw,
      typeId: selectedType?.id,
      customFields: customMap
    };

   this.conventionService.createConvention(dto).subscribe({
  next: res => {
    console.log('Convention créée', res);
    this.successMessage = "✅ Convention ajoutée avec succès !";

    // Optionnel : reset du formulaire
    this.conventionForm.reset();
    this.selectedTypeCode = null;
  },
  error: err => {
    console.error('❌ Erreur', err);
    this.successMessage = "❌ Une erreur est survenue lors de l'ajout.";
  }
});


    
  }

  onTypeChange(): void {
  const selectedCode = this.conventionForm.get('typeCode')?.value;
  this.selectedTypeCode = selectedCode;

  // Optionnel : réinitialiser les champs spécifiques quand on change de type
  if (selectedCode === 'ECHANGES') {
    this.conventionForm.patchValue({
      perimetre: '',
      modalite: ''
    });
  } else if (selectedCode === 'ACCORD') {
    this.conventionForm.patchValue({
      natureEchange: '',
      modaliteEchange: '',
      logistique: '',
      assuranceResponsabilite: '',
      renouvellement: '',
      resiliation: ''
    });
  }
}
}
