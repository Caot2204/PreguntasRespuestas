/*
 * Copyright (C) 2020 Carlos Onorio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mx.pr.almacenamiento.entidadesjpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 *
 * @author Carlos Onorio
 */
@Embeddable
public class RespuestaPK implements Serializable {

    @Basic(optional = false)
    @Lob
    @Column(name = "letra")
    private byte[] letra;
    @Basic(optional = false)
    @Lob
    @Column(name = "pregunta")
    private byte[] pregunta;
    @Basic(optional = false)
    @Lob
    @Column(name = "cuestionario")
    private byte[] cuestionario;

    public RespuestaPK() {
    }

    public RespuestaPK(byte[] letra, byte[] pregunta, byte[] cuestionario) {
        this.letra = letra;
        this.pregunta = pregunta;
        this.cuestionario = cuestionario;
    }

    public byte[] getLetra() {
        return letra;
    }

    public void setLetra(byte[] letra) {
        this.letra = letra;
    }

    public byte[] getPregunta() {
        return pregunta;
    }

    public void setPregunta(byte[] pregunta) {
        this.pregunta = pregunta;
    }

    public byte[] getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(byte[] cuestionario) {
        this.cuestionario = cuestionario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (letra != null ? letra.hashCode() : 0);
        hash += (pregunta != null ? pregunta.hashCode() : 0);
        hash += (cuestionario != null ? cuestionario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RespuestaPK)) {
            return false;
        }
        RespuestaPK other = (RespuestaPK) object;
        if ((this.letra == null && other.letra != null) || (this.letra != null && !this.letra.equals(other.letra))) {
            return false;
        }
        if ((this.pregunta == null && other.pregunta != null) || (this.pregunta != null && !this.pregunta.equals(other.pregunta))) {
            return false;
        }
        if ((this.cuestionario == null && other.cuestionario != null) || (this.cuestionario != null && !this.cuestionario.equals(other.cuestionario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.pr.almacenamiento.entidadesjpa.RespuestaPK[ letra=" + letra + ", pregunta=" + pregunta + ", cuestionario=" + cuestionario + " ]";
    }

}
