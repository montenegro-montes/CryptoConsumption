setwd("/Users/joseamontenegromontes/Desktop/medidasconsumoporconectorservo_200218");


path_mota<- "./DATOSFINALES_waspmote_rsa1_100e_200218.csv"
mota <- read.csv2(path_mota,header = FALSE)



time  <-mota [,1]
consumo <-mota [,2]
signal <-mota [,3]

x<-c(time[1],time[1040])
y<-c(consumo[1],consumo[245])


plot(c(time), c(consumo), pch=16, axes=TRUE, ylim=y, xlab="", ylab="",xlim=x,type="l",col="red")
title(main="Waspmote",xlab="Time", ylab="Consumption")
box()
par(new=TRUE)
plot(c(time), c(signal), pch=15,  xlab="", ylab="",  axes=FALSE, type="l", xlim=x, col="blue")
#axis(4,col="blue")  ## las=1 makes horizontal labels
#axis(2, col.axis="red")

mtext("Control Signal", side=4, col="blue")

library(pracma)




###################  CERO
time_0<-time[58]-time[13]                     #0.00504 CERO
time0v_1<-c(time[13:58])          
signal_0<-c(signal[13:58])
consumo_0<-c(consumo[13:58])                  
c0_1<-trapz(time0v_1,consumo_0)                #0.000117126 Julios 
                                               #0.117126 miliJulios    


###################  AES 192
time_1<-time[74]-time[59]                     #0.00168 AES 192
time128v_1<-c(time[59:74])
signal_1<-c(signal[59:74])
consumo_1<-c(consumo[59:74])
c1_128<-trapz(time128v_1,consumo_1)            #4.1842e-05 Julios 
                                               #0.0041842 miliJulios   



###################  CERO
time_0_2<-time[108]-time[75]         #0.00369 0
time0v_2<-c(time[75:108])
signal_0_2<-c(signal[75:108])
consumo_0_1<-c(consumo[75:108])
c1_256<-trapz(time0v_2,consumo_0_1)          #0.00031227 Julios
                                               #0.31227 miliJulios



###################  AES 256
time_1_2    <-time[127]-time[109]            #0.002 AES 256
time1v_2<-c(time[109:127])
signal_1_2  <-c(signal[109:127])
consumo_1_2 <-c(consumo[109:127])
c1_2        <-trapz(time1v_2,consumo_1_2)    #5.0105e-05 Julios
                                             #0.0050105   miliJulios                                          

###################  CERO
time_1_3<-time[246]-time[128]             #0.0133 CERO
time1v_3<-c(time[128:246])
signal_1_3<-c(signal[128:246])
consumo_1_3<-c(consumo[128:246])
c2_128<-trapz(time1v_3,consumo_1_3)          #0.000308395 Julios
                                             #0.308395 miliJulios


###################  AES
time_1_4    <-time[260]-time[247]           #0.0014 AES 128
signal_1_4  <-c(signal[247:260])
time1v_4<-c(time[247:260])
consumo_1_4 <-c(consumo[247:260])
c1_4        <-trapz(time1v_4,consumo_1_4)    #3.516e-05 Julios
                                             #0.03516 miliJulios



start_up<-1
end_up<-0
start_down<-1
end_down<-0
down<-FALSE;

len <- length(time)

for (i in 1:len){

  
  if (signal[i]==1 && down){
     down=FALSE
     end_down<-i-1
     start_up<-i
     timez<-time[end_down]-time[start_down]

     
     timeV    <-c(time[start_down:end_down])
     consumoV <-c(consumo[start_down:end_down])
     cJulios  <-trapz(timeV,consumoV)    
     
    # texto <- paste('Cero ',timez, ' ',start_down, ' - ',end_down)
     texto <- paste('Cero \t;',timez,';',cJulios)
     cat(texto,"\n")     
     
     if (timez==0) break
   
  } 
  
  if (signal[i]==0 && !down){
    end_up<-i-1
    timez<-time[end_up]-time[start_up]
    
    timeV    <-c(time[start_up:end_up])
    consumoV <-c(consumo[start_up:end_up])
    cJulios  <-trapz(timeV,consumoV)    
    
    #texto <- paste('Uno  \t',timez, ' ',start_up, ' - ',end_up)
    texto <- paste('Uno  \t;',timez,';',cJulios)
    cat(texto,"\n")
    down<-TRUE
    start_down<-i
    if (timez==0) break
  }
}


time[16384]-time[1]


