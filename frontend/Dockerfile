# Etapa 1: Build 
FROM node:22-alpine AS build

WORKDIR /app

# Copiar dependencias primero (caché de capas)
COPY package.json package-lock.json ./
RUN npm ci

# Copiar código fuente y compilar
COPY . .
RUN npm run build

# Etapa 2: Runtime 
FROM nginx:1.25-alpine

# Copiar build de Vite al directorio de Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Copiar configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]