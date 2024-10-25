import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { ParentComponent } from '../parent/parent.component';

@Component({
  selector: 'app-child',
  templateUrl: './child.component.html',
  styleUrls: ['./child.component.css']
})
export class ChildComponent implements OnInit, OnDestroy {
  @Input() savedData: {
    field1: string;
    field2: string;
    field3: string;
    arrayField: string[];
  } = {
    field1: '',
    field2: '',
    field3: '',
    arrayField: []
  };

  @Output() dataSaved = new EventEmitter<any>();

  field1 = '';
  field2 = '';
  field3 = '';
  arrayField: string[] = [];
  newArrayItem = '';

  constructor(private parentComponent: ParentComponent) {}

  ngOnInit() {
    // Initialize with saved data if available
    this.field1 = this.savedData.field1;
    this.field2 = this.savedData.field2;
    this.field3 = this.savedData.field3;
    this.arrayField = [...this.savedData.arrayField];

    // Override parent's requestChildData method
    this.parentComponent.requestChildData = () => {
      this.saveData();
    };
  }

  ngOnDestroy() {
    this.saveData();
  }

  saveData() {
    const dataToSave = {
      field1: this.field1,
      field2: this.field2,
      field3: this.field3,
      arrayField: [...this.arrayField]
    };
    this.dataSaved.emit(dataToSave);
  }

  addArrayItem() {
    if (this.newArrayItem) {
      this.arrayField.push(this.newArrayItem);
      this.newArrayItem = '';
    }
  }

  removeArrayItem(index: number) {
    this.arrayField.splice(index, 1);
  }
}