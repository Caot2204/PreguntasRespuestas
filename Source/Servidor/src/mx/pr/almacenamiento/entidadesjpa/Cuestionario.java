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
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Carlos Onorio
 */
@Entity
@Table(name = "cuestionarios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cuestionario.findAll", query = "SELECT c FROM Cuestionario c")
    , @NamedQuery(name = "Cuestionario.findByVecesJugado", query = "SELECT c FROM Cuestionario c WHERE c.vecesJugado = :vecesJugado")})
public class Cuestionario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Lob
    @Column(name = "id")
    private byte[] id;
    @Basic(optional = false)
    @Lob
    @Column(name = "nombre")
    private byte[] nombre;
    @Basic(optional = false)
    @Column(name = "veces_jugado")
    private int vecesJugado;
    @Lob
    @Column(name = "ultimo_ganador")
    private byte[] ultimoGanador;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cuestionario1")
    private Collection<Respuesta> respuestaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cuestionario1")
    private Collection<Pregunta> preguntaCollection;
    @JoinColumn(name = "creador", referencedColumnName = "nombre")
    @ManyToOne(optional = false)
    private Usuario creador;

    public Cuestionario() {
    }

    public Cuestionario(byte[] id) {
        this.id = id;
    }

    public Cuestionario(byte[] id, byte[] nombre, int vecesJugado) {
        this.id = id;
        this.nombre = nombre;
        this.vecesJugado = vecesJugado;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getNombre() {
        return nombre;
    }

    public void setNombre(byte[] nombre) {
        this.nombre = nombre;
    }

    public int getVecesJugado() {
        return vecesJugado;
    }

    public void setVecesJugado(int vecesJugado) {
        this.vecesJugado = vecesJugado;
    }

    public byte[] getUltimoGanador() {
        return ultimoGanador;
    }

    public void setUltimoGanador(byte[] ultimoGanador) {
        this.ultimoGanador = ultimoGanador;
    }

    @XmlTransient
    public Collection<Respuesta> getRespuestaCollection() {
        return respuestaCollection;
    }

    public void setRespuestaCollection(Collection<Respuesta> respuestaCollection) {
        this.respuestaCollection = respuestaCollection;
    }

    @XmlTransient
    public Collection<Pregunta> getPreguntaCollection() {
        return preguntaCollection;
    }

    public void setPreguntaCollection(Collection<Pregunta> preguntaCollection) {
        this.preguntaCollection = preguntaCollection;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cuestionario)) {
            return false;
        }
        Cuestionario other = (Cuestionario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.pr.almacenamiento.entidadesjpa.Cuestionario[ id=" + id + " ]";
    }
    
}
