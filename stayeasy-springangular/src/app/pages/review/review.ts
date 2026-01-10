import {Component} from '@angular/core';
import { NgForOf, NgIf } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { LoginService } from '../../service/login-service';
import { AuthService } from '../../service/auth-service';
import { PropertyResponseDTO } from '../../models/property.models';
import {PropertyService} from '../../service/property-service';
import {CreatePropertyModal} from '../create-property-modal/create-property-modal';
import {FormsModule} from '@angular/forms';

 @Component({
   selector: 'property-review',
   templateUrl: './review.html',
   styleUrls: ['./review.css'],
   standalone: true
 })
export class ReviewPage {

 }
