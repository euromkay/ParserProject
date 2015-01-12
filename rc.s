/*
 * Generated Mon Jan 12 10:42:51 PST 2015
 */

	.section ".rodata"
	.align 4
_endl:	.asciz "\n"
_intFmt:	.asciz "%d"
_strFmt:	.asciz "%s"
_boolT:	.asciz "true"
_boolF:	.asciz "false"
arrrray_error:	.asciz "Index value of %d is outside legal range [0,%d).\n"
del_errrror:	.asciz "Attempt to dereference NULL pointer.\n"

	.section ".data"
	.align 4
.globalInit:	.word 0

! ---main---
	.section ".text"
	.align 4
	.global main

main:
	set			SAVE.main, %g1
	save		%sp, %g1, %sp

	set			.globalInit, %l1
	ld			[%l1], %l1
	cmp			%l1, %g0
	bne			.globalFinish
	nop

	set			.globalInit, %l1
	set			1, %l3
	st			%l3, [%l1]
.globalFinish:


!PointerType@14ae5a5 x

!PointerType@14ae5a5 x

	mov			%fp-4, %l5
	ld			[%l5], %l2
	ld			[%l2], %l2
	mov			%fp-8, %l6
	st			%l2, [%l6]
	set			1, %l1
	mov			%fp-12, %l3
	st			%l1, [%l3]
!1
	mov			%fp-8, %l7
	mov			%fp-12, %l6
	ld			[%l6], %l0
	st			%l0, [%l7]
!x = 1
!x

	mov			%fp-4, %l3
	ld			[%l3], %l4
	cmp			%g0, %l4
	bne			.deleteAttempt0good
	nop
	set			del_errrror, %o0
	call		printf
	nop
	set			1, %o0
	call		exit
	nop
.deleteAttempt0good:
	call		free
	nop
	mov			%fp-4, %l5
	st			%g0, [%l5]
!delete x

	set			0, %l6
	mov			%fp-16, %l0
	st			%l6, [%l0]
!0
	mov			%fp-16, %l1
	ld			[%l1], %i0
	ret
	restore


!return 0

