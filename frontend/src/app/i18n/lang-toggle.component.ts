import { Component, inject } from '@angular/core';
import { LanguageService } from './language.service';

/**
 * Selector de idioma ES | EN. El espanol es el idioma por defecto de la
 * aplicacion; el ingles existe para que el sistema pueda mostrarse y
 * documentarse en ese idioma sin alterar la experiencia de sus usuarios.
 */
@Component({
  selector: 'app-lang-toggle',
  standalone: true,
  template: `
    <div class="lang-toggle" role="group" [attr.aria-label]="i18n.t('lang.switch')">
      <button
        type="button"
        class="lang-btn"
        [class.lang-btn--active]="i18n.lang() === 'es'"
        (click)="i18n.setLang('es')"
        [attr.aria-pressed]="i18n.lang() === 'es'">
        {{ i18n.t('lang.spanish') }}
      </button>
      <button
        type="button"
        class="lang-btn"
        [class.lang-btn--active]="i18n.lang() === 'en'"
        (click)="i18n.setLang('en')"
        [attr.aria-pressed]="i18n.lang() === 'en'">
        {{ i18n.t('lang.english') }}
      </button>
    </div>
  `,
  styles: [`
    .lang-toggle {
      display: inline-flex;
      align-items: center;
      gap: 2px;
      padding: 2px;
      border: 1px solid var(--border, #d7dce3);
      border-radius: 999px;
      background: var(--surface, #fff);
    }
    .lang-btn {
      min-width: 34px;
      padding: 3px 8px;
      border: 0;
      border-radius: 999px;
      background: transparent;
      color: var(--text-muted, #64748b);
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.04em;
      line-height: 1.4;
      cursor: pointer;
      transition: background-color 0.15s ease, color 0.15s ease;
    }
    .lang-btn:hover { color: var(--text, #0f172a); }
    .lang-btn--active {
      background: var(--primary, #0b7a3b);
      color: #fff;
    }
    .lang-btn--active:hover { color: #fff; }
  `],
})
export class LangToggleComponent {
  readonly i18n = inject(LanguageService);
}
