import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

interface ApiErrorResponse {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  validationErrors?: Record<string, string>;
}

@Injectable({
  providedIn: 'root'
})
export class ApiErrorService {

  getMessage(error: unknown, fallback: string = 'Something went wrong. Please try again.'
  ): string {

    if (!(error instanceof HttpErrorResponse)) {
      return fallback;
    }

    const body = error.error as string | ApiErrorResponse | null | undefined;

    if (!body) {
      return fallback;
    }

    if (typeof body === 'string') {
      return body.trim() ? body : fallback;
    }

    if (body.validationErrors && Object.keys(body.validationErrors).length > 0) {
      return Object.values(body.validationErrors).join('\n');
    }

    if (body.message) {
      return body.message;
    }

    if (body.error) {
      return body.error;
    }

    return fallback;
  }

  getValidationErrors(error: unknown): string[] {
    if (!(error instanceof HttpErrorResponse)) {
      return [];
    }

    const body = error.error as string | ApiErrorResponse | null | undefined;

    if (!body || typeof body === 'string') {
      return [];
    }

    if (!body.validationErrors) {
      return [];
    }

    return Object.values(body.validationErrors);
  }
}
