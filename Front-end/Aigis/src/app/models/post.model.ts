export class PostModel {
    constructor(
        public id: string,
        public content: string,
        public likes: number,
        public date: number,
        public imageId?: string,
    ) {}
}
