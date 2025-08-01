import { Component, OnInit } from '@angular/core';
import { AppConvention, ConventionServiceService } from '../../../Services/convention-service.service';
import { AppConventionType } from '../../../Services/convention-type.service';
import { saveAs } from 'file-saver';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { PermissionRequest } from '../../../Services/user-service.service';
import { AuthService } from '../../../Services/auth.service';
import { of, tap } from 'rxjs';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DocumentsService } from '../../../Services/documents.service';

@Component({
  selector: 'app-conventions',
  standalone: false,
  templateUrl: './conventions.component.html',
  styleUrl: './conventions.component.css',
  
})
export class ConventionsComponent implements OnInit{
conventions: AppConvention[] = [];
  selectedConventionId: number | null = null;
filters = {
  code: '',
  title: '',
  startDateFrom: '',
  startDateTo: ''
};
showFilter = false;
conventionTypes: AppConventionType[] = [];
userPermissions: PermissionRequest | null = null;
 editFormVisible = false;
 editConventionId: number | null = null;
  conventionForm!: FormGroup;
typeIdSelected: number | null = null;
  documentName: string = '';
  constructor(private conventionService: ConventionServiceService, private fb: FormBuilder,private permissionService:AuthService, private fileService: DocumentsService) {}
today: string = new Date().toISOString().split('T')[0];
  ngOnInit(): void {
     this.permissionService.getUserPermissionsAsync().subscribe(perms => {
    this.userPermissions = perms;
  });
     this.loadTypes();
  this.filterConventions();
    this.conventionService.getAllConventions().subscribe({
      next: (data) => {
        this.conventions = data;
      },
      error: (err) => {
        console.error('Erreur chargement des conventions :', err);
      }
    });  

  }

  toggleDetails(id: number): void {
    this.selectedConventionId = this.selectedConventionId === id ? null : id;
  }
  getObjectKeys(obj: any): string[] {
  return obj ? Object.keys(obj) : [];
}

loadTypes() {
  this.conventionService.getConventionTypes().subscribe(types => {
    this.conventionTypes = types;
  });
}

filterConventions() {
  this.conventionService.filterConventions(this.filters).subscribe({
    next: (res) => {
      this.conventions = res.content;
    },
    error: (err) => {
      console.error('Erreur de filtrage :', err);
    }
  });
}



showModal = false;
selectedConvention: any = null;

openModal(conv: any): void {
  this.selectedConvention = conv;
  this.showModal = true;
}

closeModal(): void {
  this.showModal = false;
  this.selectedConvention = null;
}
//Pour la modifications
  initForm() {
    this.conventionForm = this.fb.group({
      typeId: [null],
      title: [''],
      conventionNumber: [''],
      object: [''],
      signatureDate: [''],
      startDate: [''],
      endDate: [''],
      partners: [''],
      filePath: [''],
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
  }
get customFields(): FormArray {
  return this.conventionForm.get('customFields') as FormArray;
}
addCustomField() {
  this.customFields.push(this.fb.group({
    key: ['', Validators.required],
    value: ['']
  }));
}
loadConventions() {
    this.conventionService.getAllConventions().subscribe({
      next: (data) => {
        this.conventions = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des conventions', err);
        alert('Erreur lors du chargement des conventions');
      }
    });
  }
openEditForm(id: number) {
  this.editFormVisible = true;
  this.editConventionId = id;
   this.initForm();

  const typesLoaded$ = this.conventionTypes.length
    ? of(this.conventionTypes)
    : this.conventionService.getConventionTypes().pipe(
        tap(types => this.conventionTypes = types)
      );

  typesLoaded$.subscribe({
    next: () => {
      
      this.conventionService.getConventionById(id).subscribe({
        next: (conv) => {
          const matchedType = this.conventionTypes.find(t => t.code === conv.typeCode);
const matchedTypeId = matchedType?.id ?? null;
          this.conventionForm.patchValue({
           typeId: matchedTypeId,
            title: conv.title || '',
            conventionNumber: conv.conventionNumber || '',
            object: conv.object || '',
            signatureDate: conv.signatureDate?.substring(0, 10) || '',
            startDate: conv.startDate?.substring(0, 10) || '',
            endDate: conv.endDate?.substring(0, 10) || '',
            partners: conv.partners || '',
            filePath: conv.filePath || '',
            natureEchange: (conv as any).natureEchange || '',
            modaliteEchange: (conv as any).modaliteEchange || '',
            logistique: (conv as any).logistique || '',
            assuranceResponsabilite: (conv as any).assuranceResponsabilite || '',
            renouvellement: (conv as any).renouvellement || '',
            resiliation: (conv as any).resiliation || '',
            perimetre: (conv as any).perimetre || '',
            modalite: (conv as any).modalite || ''
          });
this.typeIdSelected = matchedTypeId;
          this.customFields.clear();
          if (conv.customFields) {
            for (const [key, value] of Object.entries(conv.customFields)) {
              this.customFields.push(this.fb.group({
                key: [key, Validators.required],
                value: [value]
              }));
            }
          }

          if (this.customFields.length === 0) {
            this.addCustomField();
          }
        },
        error: (err) => {
          console.error('Erreur chargement convention pour édition', err);
          alert('Impossible de charger la convention pour modification.');
        }
      });
    },
    error: (err) => {
      console.error('Erreur chargement des types', err);
    }
  });
   this.conventionForm.get('typeId')?.valueChanges.subscribe((val) => {
    this.typeIdSelected = val;
    console.log('Type sélectionné par utilisateur :', val);
  });
}





submitEdit() {
  if (this.conventionForm.invalid) {
    alert('Formulaire invalide');
    return;
  }

  const formValue = this.conventionForm.value;

  
  const customFieldsObj: Record<string, any> = {};
  formValue.customFields.forEach((field: {key: string, value: any}) => {
    if (field.key) {
      customFieldsObj[field.key] = field.value;
    }
  });

  const payload = {
    ...formValue,
    customFields: customFieldsObj
  };

  console.log('Payload envoyé:', payload);

  this.conventionService.updateConvention(this.editConventionId!, payload).subscribe({
    next: () => {
      alert('Mise à jour réussie');
      this.editFormVisible = false;
      this.editConventionId = null;
      this.loadConventions();
    },
    error: (err) => {
      console.error('Erreur mise à jour', err);
      alert('Erreur lors de la mise à jour.');
    }
  });
}
  cancelEdit() {
    this.editFormVisible = false;
    this.editConventionId = null;
  }

removeCustomField(index: number) {
  this.customFields.removeAt(index);
}
//Suppression
confirmDelete(id: number) {
  const confirmation = confirm("Êtes-vous sûr de vouloir supprimer cette convention ?");
  if (confirmation) {
    this.conventionService.deleteConvention(id).subscribe({
      next: () => {
        alert("Convention supprimée avec succès !");
        this.loadConventions(); 
      },
      error: (err) => {
        console.error("Erreur lors de la suppression", err);
        alert("Erreur lors de la suppression de la convention.");
      }
    });
  }
}

  showModalforDoc = false;
  openDocumentModal(convention: AppConvention) {
    this.selectedConventionId = convention.id;
    this.documentName = '';
    this. showModalforDoc = true;
  }
  submitDocument() {
    if (!this.selectedConventionId || !this.documentName.trim()) {
      alert('Veuillez entrer un nom de document.');
      return;
    }

    this.fileService.addDocument(this.selectedConventionId, this.documentName).subscribe({
      next: res => {
        alert('Document généré avec succès');
        this. showModalforDoc= false;
      },
   error: err => {
      console.error('Erreur complète:', err);
      let errorMessage = 'Erreur inconnue';
      if (err.error) {
        if (typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.error.error) {
          errorMessage = err.error.error;
        } else if (err.message) {
          errorMessage = err.message;
        }
      }
      alert('Erreur lors de la génération du document : ' + errorMessage);
    }

    });
  }
}
