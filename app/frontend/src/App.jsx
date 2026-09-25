import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { BusinessProvider } from './context/BusinessContext'
import MainLayout from './components/layout/MainLayout'
import ProtectedRoute, { RutaPublica, RequiereNegocio } from './components/layout/ProtectedRoute'

import LoginPage from './modules/auth/LoginPage'
import RegistroPage from './modules/auth/RegistroPage'
import NegociosPage from './modules/negocios/NegociosPage'
import DashboardPage from './modules/dashboard/DashboardPage'
import NegocioPage from './modules/negocio/NegocioPage'
import OfertasListPage from './modules/negocio/ofertas/OfertasListPage'
import OfertaFormPage from './modules/negocio/ofertas/OfertaFormPage'
import ResultadosPage from './modules/negocio/resultados/ResultadosPage'
import InteligenciaPage from './modules/inteligencia/InteligenciaPage'
import FodaPage from './modules/inteligencia/FodaPage'
import SmartPage from './modules/inteligencia/SmartPage'
import IniciativasPage from './modules/iniciativas/IniciativasPage'
import IniciativaDetallePage from './modules/iniciativas/IniciativaDetallePage'
import PlanPage from './modules/plan/PlanPage'
import AjustesPlanPage from './modules/plan/AjustesPlanPage'
import SemanaPage from './modules/plan/SemanaPage'
import EvaluacionPage from './modules/evaluacion/EvaluacionPage'
import ConfiguracionPage from './modules/sistema/ConfiguracionPage'

export default function App() {
  return (
    <AuthProvider>
      <BusinessProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<RutaPublica />}>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/registro" element={<RegistroPage />} />
            </Route>

            <Route element={<ProtectedRoute />}>
              <Route path="/negocios" element={<NegociosPage />} />

              <Route element={<RequiereNegocio />}>
                <Route element={<MainLayout />}>
                  <Route path="/" element={<DashboardPage />} />

                  <Route path="/negocio" element={<NegocioPage />} />
                  <Route path="/negocio/ofertas" element={<OfertasListPage />} />
                  <Route path="/negocio/ofertas/nuevo" element={<OfertaFormPage />} />
                  <Route path="/negocio/ofertas/:id/editar" element={<OfertaFormPage />} />
                  <Route path="/negocio/resultados" element={<ResultadosPage />} />

                  <Route path="/inteligencia" element={<InteligenciaPage />} />
                  <Route path="/inteligencia/foda" element={<FodaPage />} />
                  <Route path="/inteligencia/smart" element={<SmartPage />} />

                  <Route path="/iniciativas" element={<IniciativasPage />} />
                  <Route path="/iniciativas/:id" element={<IniciativaDetallePage />} />

                  <Route path="/plan" element={<PlanPage />} />
                  <Route path="/plan/ajustes" element={<AjustesPlanPage />} />
                  <Route path="/plan/semana/:numero" element={<SemanaPage />} />

                  <Route path="/evaluacion" element={<EvaluacionPage />} />
                  <Route path="/configuracion" element={<ConfiguracionPage />} />
                </Route>
              </Route>
            </Route>
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </BusinessProvider>
    </AuthProvider>
  )
}
