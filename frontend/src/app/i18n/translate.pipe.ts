import { Pipe, PipeTransform, inject } from '@angular/core';
import { LanguageService } from './language.service';

/**
 * Translates a key from the dictionary: `{{ 'login.title' | t }}`.
 *
 * It is deliberately an impure pipe: when the language changes, every
 * translation in the view must be re-evaluated, and a pure pipe would not run again. The
 * cost is negligible for an application this size.
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
