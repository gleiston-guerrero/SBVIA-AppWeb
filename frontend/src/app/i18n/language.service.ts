import { Injectable, signal } from '@angular/core';

export type Lang = 'es' | 'en';

const STORAGE_KEY = 'sbvia.lang';

/**
 * Diccionario de traducciones. `es` es el idioma por defecto y tambien el
 * respaldo: si una clave falta en `en`, se devuelve la version en espanol en
 * lugar de la clave cruda, de modo que un olvido degrada a espanol y nunca
 * deja texto roto en pantalla.
 */
export const TRANSLATIONS: Record<Lang, Record<string, string>> = {
  es: {
    'common.accept': 'Aceptar',
    'common.cancel': 'Cancelar',
    'common.save': 'Guardar',
    'common.saving': 'Guardando...',
    'common.close': 'Cerrar',
    'common.search': 'Buscar',
    'common.loading': 'Cargando...',
    'common.noData': 'Sin datos',
    'common.optional': 'Opcional',

    'lang.spanish': 'ES',
    'lang.english': 'EN',
    'lang.switch': 'Cambiar idioma',

    // ---------------------------------------------------------------- Acceso
    'login.taglineTitle1': 'Simulador de',
    'login.taglineTitle2': 'Comportamiento Vial',
    'login.taglineText':
      'Entrena, evalúa y mejora tus habilidades de conducción en un entorno seguro e inteligente.',
    'login.featureRealtime': 'Simulaciones en tiempo real',
    'login.featureAi': 'IA adaptativa',
    'login.featureAnalysis': 'Análisis de desempeño',
    'login.title': 'Iniciar Sesión',
    'login.subtitle': 'Accede a tu cuenta para continuar',
    'login.userLabel': 'Usuario o correo electrónico',
    'login.userPlaceholder': 'Ingresa tu usuario o correo',
    'login.passwordLabel': 'Contraseña',
    'login.submit': 'Iniciar Sesión',
    'login.noAccount': '¿No tienes cuenta?',
    'login.registerLink': 'Regístrate aquí',
    'login.errorRequired': 'Por favor complete todos los campos',
    'login.errorCredentials': 'Credenciales inválidas',
    'login.errorServer': 'Error en el servidor. Intente más tarde.',
    'app.name': 'Simulador de Comportamiento Vial',

    // ---------------------------------------------------------------- Registro
    'register.title': 'Crear Cuenta',
    'register.subtitle': 'Completa tus datos para registrarte',
    'register.firstName': 'Nombres',
    'register.lastName': 'Apellidos',
    'register.email': 'Correo electrónico',
    'register.phone': 'Teléfono',
    'register.username': 'Nombre de usuario',
    'register.password': 'Contraseña',
    'register.confirmPassword': 'Confirmar contraseña',
    'register.submit': 'Registrarme',
    'register.hasAccount': '¿Ya tienes cuenta?',
    'register.loginLink': 'Inicia sesión aquí',

    // ------------------------------------------------------------------- Shell
    'nav.dashboard': 'Panel',
    'nav.scenarios': 'Escenarios',
    'nav.practices': 'Prácticas',
    'nav.simulator': 'Simulador',
    'nav.users': 'Usuarios',
    'nav.rules': 'Reglas de tránsito',
    'nav.instructor': 'Instructor',
    'nav.audit': 'Auditoría',
    'nav.backups': 'Respaldos',
    'shell.roleAdmin': 'Administrador',
    'shell.roleInstructor': 'Instructor',
    'shell.roleDriver': 'Conductor',
    'shell.editProfile': 'Editar Perfil',
    'shell.logout': 'Cerrar Sesión',
    'shell.profileTitle': 'Editar Perfil',
    'shell.firstName': 'Nombres',
    'shell.firstNamePlaceholder': 'Ingresa tus nombres',
    'shell.firstNameRequired': 'Los nombres son obligatorios.',
    'shell.lastName': 'Apellidos',
    'shell.lastNamePlaceholder': 'Ingresa tus apellidos',
    'shell.lastNameRequired': 'Los apellidos son obligatorios.',
    'shell.phone': 'Teléfono',
    'shell.phonePlaceholder': 'Ingresa tu teléfono (Opcional)',
    'shell.saveChanges': 'Guardar Cambios',

    // --------------------------------------------------------------- Dashboard
    'dashboard.title': 'Panel de Control',
    'dashboard.welcome': 'Bienvenido',
    'dashboard.recentSimulations': 'Simulaciones recientes',
    'dashboard.averageScore': 'Puntaje promedio',
    'dashboard.totalPractices': 'Prácticas realizadas',
    'dashboard.suggestions': 'Sugerencias de mejora',
    'dashboard.eyebrow': 'Panel de control',
    'dashboard.hello': 'Hola',
    'dashboard.heroText': 'Entrena, analiza tu desempeño y mejora tus decisiones al conducir.',
    'dashboard.startPractice': 'Iniciar nueva práctica',
    'dashboard.statPractices': 'Prácticas realizadas',
    'dashboard.statAverage': 'Puntaje promedio',
    'dashboard.statApproval': 'Tasa de aprobación',
    'dashboard.statScenarios': 'Escenarios disponibles',
    'dashboard.performance': 'Análisis de rendimiento',
    'dashboard.viewHistory': 'Ver historial',
    'dashboard.approvedPractices': 'Prácticas aprobadas',
    'dashboard.emptyTip': 'Tus indicadores aparecerán después de completar la primera práctica.',
    'dashboard.personalTip': 'Recomendación personalizada',
    'dashboard.nextStep': 'Tu siguiente paso',
    'dashboard.exploreScenarios': 'Explorar escenarios',
    'dashboard.quickAccess': 'Accesos rápidos',
    'dashboard.continueTraining': 'Continúa tu entrenamiento',
    'dashboard.moduleScenarios': 'Escenarios viales',
    'dashboard.moduleScenariosText': 'Selecciona una situación y comienza a practicar.',
    'dashboard.moduleProgress': 'Mi progreso',
    'dashboard.moduleProgressText': 'Consulta resultados, promedios y prácticas.',
    'dashboard.moduleUsers': 'Administrar usuarios',
    'dashboard.moduleUsersText': 'Gestiona cuentas, accesos y roles.',
    'dashboard.tipStart': 'Empieza con un escenario de dificultad baja para establecer tu primera referencia.',
    'dashboard.tipLow': 'Repite los escenarios practicados y concéntrate en reducir las infracciones de mayor penalización.',
    'dashboard.tipMid': 'Vas por buen camino. Prueba escenarios de mayor dificultad para fortalecer tu anticipación.',
    'dashboard.tipHigh': 'Tu rendimiento es sobresaliente. Mantén la constancia con escenarios y condiciones variadas.',

    // --------------------------------------------------------------- Escenarios
    'scenarios.title': 'Escenarios viales',
    'scenarios.new': 'Nuevo escenario',
    'scenarios.name': 'Nombre',
    'scenarios.roadType': 'Tipo de vía',
    'scenarios.difficulty': 'Dificultad',
    'scenarios.weather': 'Clima',
    'scenarios.traffic': 'Densidad de tráfico',
    'scenarios.start': 'Iniciar práctica',
    'scenarios.active': 'Activo',
    'scenarios.inactive': 'Inactivo',
    'scenarios.eyebrow': 'Gestión',
    'scenarios.subtitle': 'Selecciona un escenario para practicar o administra los existentes.',
    'scenarios.all': 'Todos',
    'scenarios.edit': 'Editar',
    'scenarios.delete': 'Eliminar',
    'scenarios.empty': 'No hay escenarios registrados aún.',
    'scenarios.createFirst': 'Crear primer escenario',
    'scenarios.prev': 'Anterior',
    'scenarios.next': 'Siguiente',
    'scenarios.page': 'Página',
    'scenarios.of': 'de',
    'scenarios.deleteTitle': 'Eliminar escenario',
    'scenarios.deleteQuestion': '¿Deseas eliminar',
    'scenarios.deleteWarning': 'Dejará de aparecer para los conductores, pero su historial se conservará.',
    'scenarios.deleting': 'Eliminando...',
    'scenarios.confirmDelete': 'Sí, eliminar',

    // --------------------------------------------------------------- Simulación
    'sim.title': 'Resultado de la práctica',
    'sim.score': 'Puntaje',
    'sim.feedback': 'Retroalimentación',
    'sim.infractions': 'Infracciones',
    'sim.passed': 'Aprobada',
    'sim.failed': 'Reprobada',
    'sim.downloadCertificate': 'Descargar certificado',
    'sim.preparing': 'Preparando la práctica...',
    'sim.back': 'Volver',
    'sim.inProgress': 'Práctica en progreso',
    'sim.currentScore': 'Puntaje actual',
    'sim.instructions': 'Registra las incidencias observadas durante el recorrido. Cada una descuenta puntos automáticamente.',
    'sim.points': 'puntos',
    'sim.savingResult': 'Guardando resultado...',
    'sim.finish': 'Finalizar práctica',
    'sim.finalReport': 'Reporte final',
    'sim.goodWork': 'Buen trabajo. Continúa practicando para mantener una conducción segura.',
    'sim.reviewInfractions': 'Revisa las infracciones registradas y vuelve a practicar este escenario.',
    'sim.viewHistory': 'Ver historial',
    'sim.chooseAnother': 'Elegir otro escenario',
    'sim.infSpeeding': 'Exceso de velocidad',
    'sim.infSignal': 'No respetar una señal',
    'sim.infLane': 'Cambio de carril inseguro',
    'sim.infBraking': 'Frenado brusco',
  },

  en: {
    'common.accept': 'Accept',
    'common.cancel': 'Cancel',
    'common.save': 'Save',
    'common.saving': 'Saving...',
    'common.close': 'Close',
    'common.search': 'Search',
    'common.loading': 'Loading...',
    'common.noData': 'No data',
    'common.optional': 'Optional',

    'lang.spanish': 'ES',
    'lang.english': 'EN',
    'lang.switch': 'Switch language',

    'login.taglineTitle1': 'Road Behaviour',
    'login.taglineTitle2': 'Simulator',
    'login.taglineText':
      'Train, assess and improve your driving skills in a safe, intelligent environment.',
    'login.featureRealtime': 'Real-time simulations',
    'login.featureAi': 'Adaptive AI',
    'login.featureAnalysis': 'Performance analysis',
    'login.title': 'Sign In',
    'login.subtitle': 'Access your account to continue',
    'login.userLabel': 'Username or email',
    'login.userPlaceholder': 'Enter your username or email',
    'login.passwordLabel': 'Password',
    'login.submit': 'Sign In',
    'login.noAccount': "Don't have an account?",
    'login.registerLink': 'Register here',
    'login.errorRequired': 'Please fill in all the fields',
    'login.errorCredentials': 'Invalid credentials',
    'login.errorServer': 'Server error. Please try again later.',
    'app.name': 'Road Behaviour Simulator',

    'register.title': 'Create Account',
    'register.subtitle': 'Fill in your details to register',
    'register.firstName': 'First names',
    'register.lastName': 'Last names',
    'register.email': 'Email address',
    'register.phone': 'Phone',
    'register.username': 'Username',
    'register.password': 'Password',
    'register.confirmPassword': 'Confirm password',
    'register.submit': 'Register',
    'register.hasAccount': 'Already have an account?',
    'register.loginLink': 'Sign in here',

    'nav.dashboard': 'Dashboard',
    'nav.scenarios': 'Scenarios',
    'nav.practices': 'Practices',
    'nav.simulator': 'Simulator',
    'nav.users': 'Users',
    'nav.rules': 'Traffic rules',
    'nav.instructor': 'Instructor',
    'nav.audit': 'Audit log',
    'nav.backups': 'Backups',
    'shell.roleAdmin': 'Administrator',
    'shell.roleInstructor': 'Instructor',
    'shell.roleDriver': 'Driver',
    'shell.editProfile': 'Edit Profile',
    'shell.logout': 'Sign Out',
    'shell.profileTitle': 'Edit Profile',
    'shell.firstName': 'First names',
    'shell.firstNamePlaceholder': 'Enter your first names',
    'shell.firstNameRequired': 'First names are required.',
    'shell.lastName': 'Last names',
    'shell.lastNamePlaceholder': 'Enter your last names',
    'shell.lastNameRequired': 'Last names are required.',
    'shell.phone': 'Phone',
    'shell.phonePlaceholder': 'Enter your phone (optional)',
    'shell.saveChanges': 'Save Changes',

    'dashboard.title': 'Dashboard',
    'dashboard.welcome': 'Welcome',
    'dashboard.recentSimulations': 'Recent simulations',
    'dashboard.averageScore': 'Average score',
    'dashboard.totalPractices': 'Practices completed',
    'dashboard.suggestions': 'Improvement suggestions',
    'dashboard.eyebrow': 'Control panel',
    'dashboard.hello': 'Hello',
    'dashboard.heroText': 'Train, analyse your performance and improve your driving decisions.',
    'dashboard.startPractice': 'Start a new practice',
    'dashboard.statPractices': 'Practices completed',
    'dashboard.statAverage': 'Average score',
    'dashboard.statApproval': 'Pass rate',
    'dashboard.statScenarios': 'Available scenarios',
    'dashboard.performance': 'Performance analysis',
    'dashboard.viewHistory': 'View history',
    'dashboard.approvedPractices': 'Practices passed',
    'dashboard.emptyTip': 'Your indicators will appear once you complete your first practice.',
    'dashboard.personalTip': 'Personalised recommendation',
    'dashboard.nextStep': 'Your next step',
    'dashboard.exploreScenarios': 'Explore scenarios',
    'dashboard.quickAccess': 'Quick access',
    'dashboard.continueTraining': 'Continue your training',
    'dashboard.moduleScenarios': 'Road scenarios',
    'dashboard.moduleScenariosText': 'Pick a situation and start practising.',
    'dashboard.moduleProgress': 'My progress',
    'dashboard.moduleProgressText': 'Check results, averages and practices.',
    'dashboard.moduleUsers': 'Manage users',
    'dashboard.moduleUsersText': 'Handle accounts, access and roles.',
    'dashboard.tipStart': 'Start with a low-difficulty scenario to set your first benchmark.',
    'dashboard.tipLow': 'Repeat the scenarios you have practised and focus on cutting the most heavily penalised infractions.',
    'dashboard.tipMid': 'You are on the right track. Try harder scenarios to sharpen your anticipation.',
    'dashboard.tipHigh': 'Your performance is outstanding. Keep it consistent across varied scenarios and conditions.',

    'scenarios.title': 'Road scenarios',
    'scenarios.new': 'New scenario',
    'scenarios.name': 'Name',
    'scenarios.roadType': 'Road type',
    'scenarios.difficulty': 'Difficulty',
    'scenarios.weather': 'Weather',
    'scenarios.traffic': 'Traffic density',
    'scenarios.start': 'Start practice',
    'scenarios.active': 'Active',
    'scenarios.inactive': 'Inactive',
    'scenarios.eyebrow': 'Management',
    'scenarios.subtitle': 'Pick a scenario to practise or manage the existing ones.',
    'scenarios.all': 'All',
    'scenarios.edit': 'Edit',
    'scenarios.delete': 'Delete',
    'scenarios.empty': 'No scenarios have been registered yet.',
    'scenarios.createFirst': 'Create the first scenario',
    'scenarios.prev': 'Previous',
    'scenarios.next': 'Next',
    'scenarios.page': 'Page',
    'scenarios.of': 'of',
    'scenarios.deleteTitle': 'Delete scenario',
    'scenarios.deleteQuestion': 'Do you want to delete',
    'scenarios.deleteWarning': 'It will stop showing up for drivers, but its history will be kept.',
    'scenarios.deleting': 'Deleting...',
    'scenarios.confirmDelete': 'Yes, delete',

    'sim.title': 'Practice result',
    'sim.score': 'Score',
    'sim.feedback': 'Feedback',
    'sim.infractions': 'Infractions',
    'sim.passed': 'Passed',
    'sim.failed': 'Not passed',
    'sim.downloadCertificate': 'Download certificate',
    'sim.preparing': 'Preparing the practice...',
    'sim.back': 'Back',
    'sim.inProgress': 'Practice in progress',
    'sim.currentScore': 'Current score',
    'sim.instructions': 'Record the incidents observed along the route. Each one deducts points automatically.',
    'sim.points': 'points',
    'sim.savingResult': 'Saving result...',
    'sim.finish': 'Finish practice',
    'sim.finalReport': 'Final report',
    'sim.goodWork': 'Good job. Keep practising to maintain safe driving.',
    'sim.reviewInfractions': 'Review the recorded infractions and practise this scenario again.',
    'sim.viewHistory': 'View history',
    'sim.chooseAnother': 'Choose another scenario',
    'sim.infSpeeding': 'Speeding',
    'sim.infSignal': 'Failing to obey a sign',
    'sim.infLane': 'Unsafe lane change',
    'sim.infBraking': 'Harsh braking',
  },
};

/**
 * Servicio de idioma. El espanol es el idioma por defecto y el de respaldo.
 * La eleccion se conserva en localStorage, de modo que la captura de pantalla
 * en ingles no depende de tocar el codigo.
 */
@Injectable({ providedIn: 'root' })
export class LanguageService {
  private readonly _lang = signal<Lang>(LanguageService.initialLang());

  readonly lang = this._lang.asReadonly();

  private static initialLang(): Lang {
    try {
      const guardado = localStorage.getItem(STORAGE_KEY);
      if (guardado === 'es' || guardado === 'en') {
        return guardado;
      }
    } catch {
      /* almacenamiento no disponible: se usa el idioma por defecto */
    }
    return 'es';
  }

  setLang(lang: Lang): void {
    this._lang.set(lang);
    try {
      localStorage.setItem(STORAGE_KEY, lang);
    } catch {
      /* sin persistencia: el cambio sigue aplicandose en memoria */
    }
    document.documentElement.setAttribute('lang', lang);
  }

  toggle(): void {
    this.setLang(this._lang() === 'es' ? 'en' : 'es');
  }

  /** Traduce una clave; si falta, devuelve el espanol y, en ultimo caso, la clave. */
  t(key: string): string {
    const lang = this._lang();
    return TRANSLATIONS[lang][key] ?? TRANSLATIONS.es[key] ?? key;
  }
}
