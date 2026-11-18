import { createContext, useContext, useEffect, useMemo, useState } from 'react'

const STORAGE_KEY = 'hostel_language'
const translations = {
  en: {
    english: 'English',
    gujarati: 'ગુજરાતી',
    overview: 'Overview',
    jobRequests: 'Job requests',
    assignedRequests: 'Assigned requests',
    createRequest: 'Create request',
    maintenanceStaff: 'Maintenance staff',
    timeSlots: 'Time slots',
    myProfile: 'My profile',
    logout: 'Logout',
    notifications: 'Notifications',
    language: 'Language',
    adminWorkspace: 'ADMIN WORKSPACE',
    workerWorkspace: 'WORKER WORKSPACE',
    studentWorkspace: 'STUDENT WORKSPACE',
    workspace: 'WORKSPACE',
    availableRequests: 'Available requests',
    signInWorkspace: 'Sign in to your workspace',
    createAccount: 'Create your hostel account',
    login: 'Login',
    signUp: 'Sign up',
    fullName: 'Full name',
    email: 'Email',
    password: 'Password',
    createAccountButton: 'Create account',
    pleaseWait: 'Please wait...',
  },
  gu: {
    english: 'English',
    gujarati: 'ગુજરાતી',
    overview: 'ઓવરવ્યૂ',
    jobRequests: 'જોબ વિનંતીઓ',
    assignedRequests: 'સોંપાયેલ વિનંતીઓ',
    createRequest: 'વિનંતી બનાવો',
    maintenanceStaff: 'જાળવણી સ્ટાફ',
    timeSlots: 'સમય સ્લોટ',
    myProfile: 'મારી પ્રોફાઇલ',
    logout: 'લૉગઆઉટ',
    notifications: 'સૂચનાઓ',
    language: 'ભાષા',
    adminWorkspace: 'એડમિન વર્કસ્પેસ',
    workerWorkspace: 'વર્કર વર્કસ્પેસ',
    studentWorkspace: 'વિદ્યાર્થી વર્કસ્પેસ',
    workspace: 'વર્કસ્પેસ',
    availableRequests: 'ઉપલબ્ધ વિનંતીઓ',
    signInWorkspace: 'તમારા વર્કસ્પેસમાં સાઇન ઇન કરો',
    createAccount: 'તમારું હોસ્ટેલ એકાઉન્ટ બનાવો',
    login: 'લૉગિન',
    signUp: 'સાઇન અપ',
    fullName: 'પૂરું નામ',
    email: 'ઇમેઇલ',
    password: 'પાસવર્ડ',
    createAccountButton: 'એકાઉન્ટ બનાવો',
    pleaseWait: 'કૃપા કરીને રાહ જુઓ...',
  },
}

const gujaratiText = {
  'Worker workspace': 'વર્કર વર્કસ્પેસ',
  'Student workspace': 'વિદ્યાર્થી વર્કસ્પેસ',
  'Admin workspace': 'એડમિન વર્કસ્પેસ',
  'Today’s schedule': 'આજનું શેડ્યૂલ',
  'Jobs assigned by your supervisor.': 'તમારા સુપરવાઇઝર દ્વારા સોંપાયેલ કામ.',
  'Awaiting student time slot': 'વિદ્યાર્થીના સમય સ્લોટની રાહ જોઈ રહ્યા છીએ',
  'No contact number': 'સંપર્ક નંબર નથી',
  'Call student': 'વિદ્યાર્થીને કૉલ કરો',
  'Not solved': 'ઉકેલાયું નથી',
  'Complete job': 'કામ પૂર્ણ કરો',
  'After photo': 'કામ પછીનો ફોટો',
  'Student ID photo': 'વિદ્યાર્થીના IDનો ફોટો',
  'Cancel': 'રદ કરો',
  'Save completion': 'પૂર્ણતા સાચવો',
  'Loading today’s schedule...': 'આજનું શેડ્યૂલ લોડ થઈ રહ્યું છે...',
  'Unable to load assigned jobs.': 'સોંપાયેલ કામ લોડ કરી શકાયું નથી.',
  'Job marked complete.': 'કામ પૂર્ણ તરીકે નોંધાયું.',
  'Job marked not solved.': 'કામ ઉકેલાયું નથી તરીકે નોંધાયું.',
  'Could not complete job.': 'કામ પૂર્ણ કરી શકાયું નથી.',
  'Could not update job.': 'કામ અપડેટ કરી શકાયું નથી.',
  'Report an issue': 'સમસ્યાની જાણ કરો',
  'Submit a maintenance complaint and track it below.': 'જાળવણીની ફરિયાદ નોંધાવો અને નીચે તેની સ્થિતિ જુઓ.',
  'Description': 'વર્ણન',
  'My Room': 'મારો રૂમ',
  'Common Area': 'સામાન્ય વિસ્તાર',
  'Block': 'બ્લોક',
  'Floor': 'માળ',
  'Room': 'રૂમ',
  'Category': 'કેટેગરી',
  'Urgency': 'તાકીદ',
  'Contact number': 'સંપર્ક નંબર',
  'Area': 'વિસ્તાર',
  'Submit issue': 'સમસ્યા નોંધાવો',
  'My complaints': 'મારી ફરિયાદો',
  'Select block': 'બ્લોક પસંદ કરો',
  'Select floor': 'માળ પસંદ કરો',
  'Select room': 'રૂમ પસંદ કરો',
  'Operations overview': 'કામગીરીનો ઓવરવ્યૂ',
  'Monitor requests, workers, and hostel maintenance activity.': 'વિનંતીઓ, કામદારો અને હોસ્ટેલની જાળવણીની પ્રવૃત્તિ જુઓ.',
  'Open requests': 'ખુલ્લી વિનંતીઓ',
  'Active': 'સક્રિય',
  'Completed': 'પૂર્ણ',
  'Resolved': 'ઉકેલાયેલ',
  'Staff on duty': 'ડ્યુટી પરનો સ્ટાફ',
  'Available now': 'હમણાં ઉપલબ્ધ',
  'Recent job requests': 'તાજેતરની કામની વિનંતીઓ',
  'Track and manage the latest maintenance requests.': 'તાજેતરની જાળવણી વિનંતીઓ જુઓ અને સંચાલિત કરો.',
  'View all': 'બધું જુઓ',
  'Request': 'વિનંતી',
  'Location': 'સ્થાન',
  'Status': 'સ્થિતિ',
  'Today’s maintenance team.': 'આજની જાળવણી ટીમ.',
  'Urgent open requests': 'તાત્કાલિક ખુલ્લી વિનંતીઓ',
  'Items needing supervisor attention.': 'સુપરવાઇઝરના ધ્યાનની જરૂર હોય તેવી બાબતો.',
  'No urgent open requests.': 'કોઈ તાત્કાલિક ખુલ્લી વિનંતી નથી.',
  'Job requests': 'કામની વિનંતીઓ',
  'Review, assign, and track hostel maintenance requests.': 'હોસ્ટેલની જાળવણી વિનંતીઓ જુઓ, સોંપો અને ટ્રૅક કરો.',
  'All requests': 'બધી વિનંતીઓ',
  'requests match the current filters.': 'વિનંતીઓ હાલના ફિલ્ટર સાથે મેળ ખાય છે.',
  'Total requests': 'કુલ વિનંતીઓ',
  'Submitted': 'સબમિટ થયેલ',
  'Search requests...': 'વિનંતીઓ શોધો...',
  'All status': 'બધી સ્થિતિ',
  'All urgency': 'બધી તાકીદ',
  'Details': 'વિગતો',
  'Assigned staff': 'સોંપાયેલ સ્ટાફ',
  'Not provided': 'આપેલ નથી',
  'Entry log': 'પ્રવેશ લૉગ',
  'Assign maintenance staff': 'જાળવણી સ્ટાફ સોંપો',
  'Select available staff': 'ઉપલબ્ધ સ્ટાફ પસંદ કરો',
  'Assign staff': 'સ્ટાફ સોંપો',
  'My profile': 'મારી પ્રોફાઇલ',
  'Your account details and workspace access.': 'તમારા એકાઉન્ટની વિગતો અને વર્કસ્પેસ ઍક્સેસ.',
  'Account': 'એકાઉન્ટ',
  'Full name': 'પૂરું નામ',
  'Email': 'ઇમેઇલ',
  'Role': 'ભૂમિકા',
  'User ID': 'યુઝર ID',
  'Worker ID': 'વર્કર ID',
  'Maintenance staff': 'જાળવણી સ્ટાફ',
  'Create the worker login and profile together. The worker will receive access using these credentials.': 'વર્કરનું લૉગિન અને પ્રોફાઇલ એકસાથે બનાવો. વર્કરને આ ઓળખપત્રોથી ઍક્સેસ મળશે.',
  'Add worker': 'વર્કર ઉમેરો',
  'Temporary password': 'तात્કાલિક પાસવર્ડ',
  'Trade': 'કામનો પ્રકાર',
  'Experience (years)': 'અનુભવ (વર્ષો)',
  'Slot duration': 'સ્લોટનો સમયગાળો',
  'Shift start': 'શિફ્ટ શરૂ',
  'Shift end': 'શિફ્ટ સમાપ્ત',
  'Preferred language': 'પસંદગીની ભાષા',
  'Working days': 'કામકાજના દિવસો',
  'Available for assignments': 'કામ સોંપવા માટે ઉપલબ્ધ',
  'Has smartphone': 'સ્માર્ટફોન છે',
  'Supervisor-managed': 'સુપરવાઇઝર દ્વારા સંચાલિત',
  'Current workers': 'હાલના વર્કર્સ',
  'worker profiles': 'વર્કર પ્રોફાઇલ',
  'Create worker': 'વર્કર બનાવો',
  'Creating worker...': 'વર્કર બનાવાઈ રહ્યો છે...',
  'Sign in to your workspace': 'તમારા વર્કસ્પેસમાં સાઇન ઇન કરો',
  'Create your hostel account': 'તમારું હોસ્ટેલ એકાઉન્ટ બનાવો',
  'Login': 'લૉગિન',
  'Sign up': 'સાઇન અપ',
  'Password': 'પાસવર્ડ',
  'Please wait...': 'કૃપા કરીને રાહ જુઓ...',
  'Create account': 'એકાઉન્ટ બનાવો',
  'Student accounts only. Staff accounts are created by a supervisor.': 'માત્ર વિદ્યાર્થી એકાઉન્ટ. સ્ટાફ એકાઉન્ટ સુપરવાઇઝર બનાવે છે.',
  'Admin accounts are created by the system administrator.': 'એડમિન એકાઉન્ટ સિસ્ટમ એડમિનિસ્ટ્રેટર બનાવે છે.',
  'NORMAL': 'સામાન્ય',
  'URGENT': 'તાત્કાલિક',
  'SUBMITTED': 'સબમિટ થયેલ',
  'ASSIGNED': 'સોંપાયેલ',
  'SCHEDULED': 'શેડ્યૂલ થયેલ',
  'IN_PROGRESS': 'ચાલુ',
  'COMPLETED': 'પૂર્ણ',
  'NOT_SOLVED': 'ઉકેલાયું નથી',
  'CANCELLED': 'રદ થયેલ',
  'ELECTRICAL': 'ઇલેક્ટ્રિકલ',
  'PLUMBING': 'પ્લમ્બિંગ',
  'CARPENTRY': 'કારપેન્ટરી',
  'CLEANING': 'સફાઈ',
  'GENERAL': 'સામાન્ય',
  'Worker': 'વર્કર',
  'Student': 'વિદ્યાર્થી',
  'Admin': 'એડમિન',
  'workspace': 'વર્કસ્પેસ',
  'admin': 'એડમિન',
  'worker': 'વર્કર',
  'student': 'વિદ્યાર્થી',
  'Operations / Requests': 'કામગીરી / વિનંતીઓ',
  'Operations / Staff': 'કામગીરી / સ્ટાફ',
  'Visit window': 'મુલાકાતનો સમય',
  'Action': 'ક્રિયા',
  'No requests match these filters.': 'આ ફિલ્ટર સાથે કોઈ વિનંતી મળતી નથી.',
  'On duty': 'ડ્યુટી પર',
  'Unavailable': 'ઉપલબ્ધ નથી',
  'active jobs': 'સક્રિય કામ',
  'General': 'સામાન્ય',
  'Issue before photo': 'સમસ્યા પહેલાંનો ફોટો',
  'Entry': 'પ્રવેશ',
  'Exited': 'બહાર નીકળ્યા',
  'Inside': 'અંદર',
  'Keys returned': 'ચાવીઓ પરત',
}

const originalText = new WeakMap()
const lastTranslatedText = new WeakMap()
const originalAttributes = new WeakMap()
const lastTranslatedAttributes = new WeakMap()
const translatableAttributes = ['aria-label', 'placeholder', 'title', 'alt']
const translateString = (value, language) => {
  if (language === 'en') return value
  return Object.entries(gujaratiText).sort(([a], [b]) => b.length - a.length)
    .reduce((text, [english, gujarati]) => text.replaceAll(english, gujarati), value)
}

function translateDocument(language) {
  const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT)
  const nodes = []
  while (walker.nextNode()) nodes.push(walker.currentNode)
  nodes.forEach((node) => {
    if (originalText.has(node) && node.nodeValue !== lastTranslatedText.get(node)) originalText.set(node, node.nodeValue)
    if (!originalText.has(node)) originalText.set(node, node.nodeValue)
    node.nodeValue = translateString(originalText.get(node), language)
    lastTranslatedText.set(node, node.nodeValue)
  })
  document.querySelectorAll('*').forEach((element) => translatableAttributes.forEach((attribute) => {
    if (!element.hasAttribute(attribute)) return
    if (!originalAttributes.has(element)) originalAttributes.set(element, {})
    const attributes = originalAttributes.get(element)
    const currentValue = element.getAttribute(attribute)
    const lastValue = lastTranslatedAttributes.get(element)?.[attribute]
    if (!(attribute in attributes) || currentValue !== lastValue) attributes[attribute] = currentValue
    const translatedValue = translateString(attributes[attribute], language)
    element.setAttribute(attribute, translatedValue)
    if (!lastTranslatedAttributes.has(element)) lastTranslatedAttributes.set(element, {})
    lastTranslatedAttributes.get(element)[attribute] = translatedValue
  }))
}

function TranslationRuntime({ language }) {
  useEffect(() => {
    let translating = false
    const observer = new MutationObserver(() => {
      if (!translating) {
        translating = true
        observer.disconnect()
        translateDocument(language)
        observer.observe(document.body, { childList: true, subtree: true, characterData: true })
        translating = false
      }
    })
    translateDocument(language)
    observer.observe(document.body, { childList: true, subtree: true, characterData: true })
    return () => observer.disconnect()
  }, [language])
  return null
}

const LanguageContext = createContext(null)

const initialLanguage = () => localStorage.getItem(STORAGE_KEY) === 'gu' ? 'gu' : 'en'

export function LanguageProvider({ children }) {
  const [language, setLanguageState] = useState(initialLanguage)
  const setLanguage = (value) => {
    localStorage.setItem(STORAGE_KEY, value)
    setLanguageState(value)
  }
  const value = useMemo(() => ({ language, setLanguage, t: (key) => translations[language][key] || translations.en[key] || key }), [language])
  return <LanguageContext.Provider value={value}><TranslationRuntime language={language} />{children}</LanguageContext.Provider>
}

export const useLanguage = () => useContext(LanguageContext)
