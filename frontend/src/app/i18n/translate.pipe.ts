import { Pipe, PipeTransform, inject } from '@angular/core';
import { LanguageService } from './language.service';

/**
 * Traduce una clave del diccionario: `{{ 'login.title' | t }}`.
 *
 * Es un pipe impuro a proposito: al cambiar el idioma hay que reevaluar todas
 * las traducciones de la vista, y un pipe puro no se volveria a ejecutar. El
 * coste es despreciable para el tamano de esta aplicacion.
 */
@Pipe({
  name: 't',
  standalone: true,
  pure: false,
})
export class TranslatePipe implements PipeTransform {
  private readonly i18n = inject(LanguageService);

  transform(key: string): string {
    return this.i18n.t(key);
  }
}
